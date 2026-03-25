using Microsoft.EntityFrameworkCore;
using InventoryService.Api.Data;
using InventoryService.Api.Models;

namespace InventoryService.Api.Services;

/// <summary>
/// Core business-logic service for inventory operations.
/// Owns all stock-level reads and writes against the <see cref="InventoryDbContext"/>.
/// </summary>
public class InventoryItemService
{
    private readonly InventoryDbContext _context;

    public InventoryItemService(InventoryDbContext context)
    {
        _context = context;
    }

    /// <summary>Retrieves every inventory record.</summary>
    /// <returns>A list of all <see cref="InventoryItem"/> entities.</returns>
    public async Task<List<InventoryItem>> GetAllInventoryAsync()
    {
        return await _context.InventoryItems.ToListAsync();
    }

    /// <summary>Retrieves the inventory record for a single product.</summary>
    /// <param name="productId">Product identifier.</param>
    /// <returns>The matching item, or <c>null</c> if none exists.</returns>
    public async Task<InventoryItem?> GetInventoryByProductIdAsync(int productId)
    {
        return await _context.InventoryItems.FirstOrDefaultAsync(i => i.ProductId == productId);
    }

    /// <summary>Adds <paramref name="quantity"/> units to the product's stock and updates the restock timestamp.</summary>
    /// <param name="productId">Product identifier.</param>
    /// <param name="quantity">Units to add (must be positive).</param>
    /// <returns>The updated <see cref="InventoryItem"/>.</returns>
    /// <exception cref="ArgumentException">Thrown when no inventory record exists for the product.</exception>
    public async Task<InventoryItem> RestockAsync(int productId, int quantity)
    {
        var item = await _context.InventoryItems.FirstOrDefaultAsync(i => i.ProductId == productId)
            ?? throw new ArgumentException($"No inventory record for product {productId}");
        item.QuantityOnHand += quantity;
        item.LastRestocked = DateTime.UtcNow;
        await _context.SaveChangesAsync();
        return item;
    }

    /// <summary>Returns items whose <see cref="InventoryItem.QuantityOnHand"/> is at or below <see cref="InventoryItem.ReorderLevel"/>.</summary>
    /// <returns>A list of low-stock <see cref="InventoryItem"/> entities.</returns>
    public async Task<List<InventoryItem>> GetLowStockItemsAsync()
    {
        return await _context.InventoryItems
            .Where(i => i.QuantityOnHand <= i.ReorderLevel)
            .ToListAsync();
    }

    /// <summary>Atomically checks availability and reserves stock for multiple products in one transaction.</summary>
    /// <param name="request">Reservation request containing a list of product/quantity pairs.</param>
    /// <returns>A <see cref="StockReservationResponse"/> indicating success or failure with details.</returns>
    public async Task<StockReservationResponse> CheckAndReserveStockAsync(StockReservationRequest request)
    {
        using var transaction = await _context.Database.BeginTransactionAsync();
        try
        {
            var response = new StockReservationResponse { Success = true };

            foreach (var reqItem in request.Items)
            {
                var inventory = await _context.InventoryItems
                    .FirstOrDefaultAsync(i => i.ProductId == reqItem.ProductId);

                if (inventory is null)
                {
                    response.Success = false;
                    response.Error = $"No inventory record for product {reqItem.ProductId}";
                    await transaction.RollbackAsync();
                    return response;
                }

                if (inventory.QuantityOnHand < reqItem.Quantity)
                {
                    response.Success = false;
                    response.Error = $"Insufficient stock for product {reqItem.ProductId} ({inventory.ProductName}). Available: {inventory.QuantityOnHand}, Requested: {reqItem.Quantity}";
                    await transaction.RollbackAsync();
                    return response;
                }

                inventory.QuantityOnHand -= reqItem.Quantity;

                response.ReservedItems.Add(new ReservedItem
                {
                    ProductId = reqItem.ProductId,
                    QuantityReserved = reqItem.Quantity,
                    RemainingStock = inventory.QuantityOnHand
                });
            }

            await _context.SaveChangesAsync();
            await transaction.CommitAsync();
            return response;
        }
        catch (Exception ex)
        {
            await transaction.RollbackAsync();
            return new StockReservationResponse
            {
                Success = false,
                Error = $"Failed to reserve stock: {ex.Message}"
            };
        }
    }
}
