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

    /// <summary>Removes <paramref name="quantity"/> units from the product's stock.</summary>
    /// <param name="productId">Product identifier.</param>
    /// <param name="quantity">Units to deduct.</param>
    /// <returns>The updated <see cref="InventoryItem"/>.</returns>
    /// <exception cref="ArgumentException">Thrown when no inventory record exists for the product.</exception>
    /// <exception cref="InvalidOperationException">Thrown when available stock is less than the requested quantity.</exception>
    public async Task<InventoryItem> DeductStockAsync(int productId, int quantity)
    {
        var item = await _context.InventoryItems.FirstOrDefaultAsync(i => i.ProductId == productId)
            ?? throw new ArgumentException($"No inventory record for product {productId}");

        if (item.QuantityOnHand < quantity)
            throw new InvalidOperationException($"Insufficient stock for product {productId}. Available: {item.QuantityOnHand}");

        item.QuantityOnHand -= quantity;
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
}
