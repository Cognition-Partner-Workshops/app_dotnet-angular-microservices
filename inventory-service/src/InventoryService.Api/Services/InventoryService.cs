using Microsoft.EntityFrameworkCore;
using InventoryService.Api.Data;
using InventoryService.Api.Models;

namespace InventoryService.Api.Services;

/// <summary>
/// Core business-logic service for inventory management.
/// Encapsulates all stock-level queries and mutations against the inventory database.
/// </summary>
public class InventoryItemService
{
    private readonly InventoryDbContext _context;

    /// <summary>
    /// Initializes a new instance of <see cref="InventoryItemService"/>.
    /// </summary>
    /// <param name="context">The database context used for inventory data access.</param>
    public InventoryItemService(InventoryDbContext context)
    {
        _context = context;
    }

    /// <summary>
    /// Retrieves all inventory items from the database.
    /// </summary>
    /// <returns>A list of every <see cref="InventoryItem"/> in the system.</returns>
    public async Task<List<InventoryItem>> GetAllInventoryAsync()
    {
        return await _context.InventoryItems.ToListAsync();
    }

    /// <summary>
    /// Looks up the inventory record for a specific product.
    /// </summary>
    /// <param name="productId">The product identifier to search for.</param>
    /// <returns>The matching <see cref="InventoryItem"/>, or <c>null</c> if not found.</returns>
    public async Task<InventoryItem?> GetInventoryByProductIdAsync(int productId)
    {
        return await _context.InventoryItems.FirstOrDefaultAsync(i => i.ProductId == productId);
    }

    /// <summary>
    /// Increases the stock level for a product and updates the <see cref="InventoryItem.LastRestocked"/> timestamp.
    /// </summary>
    /// <param name="productId">The product identifier to restock.</param>
    /// <param name="quantity">The number of units to add.</param>
    /// <returns>The updated <see cref="InventoryItem"/>.</returns>
    /// <exception cref="ArgumentException">Thrown when no inventory record exists for the given <paramref name="productId"/>.</exception>
    public async Task<InventoryItem> RestockAsync(int productId, int quantity)
    {
        var item = await _context.InventoryItems.FirstOrDefaultAsync(i => i.ProductId == productId)
            ?? throw new ArgumentException($"No inventory record for product {productId}");
        item.QuantityOnHand += quantity;
        item.LastRestocked = DateTime.UtcNow;
        await _context.SaveChangesAsync();
        return item;
    }

    /// <summary>
    /// Returns all inventory items whose current quantity is at or below their reorder level.
    /// </summary>
    /// <returns>A list of <see cref="InventoryItem"/> records that need restocking.</returns>
    public async Task<List<InventoryItem>> GetLowStockItemsAsync()
    {
        return await _context.InventoryItems
            .Where(i => i.QuantityOnHand <= i.ReorderLevel)
            .ToListAsync();
    }

    /// <summary>
    /// Removes a specified quantity from a product's stock.
    /// </summary>
    /// <param name="productId">The product identifier to deduct from.</param>
    /// <param name="quantity">The number of units to remove.</param>
    /// <returns>The updated <see cref="InventoryItem"/>, or <c>null</c> if the product was not found.</returns>
    /// <exception cref="InvalidOperationException">
    /// Thrown when the requested quantity exceeds the available stock.
    /// </exception>
    public async Task<InventoryItem?> DeductStockAsync(int productId, int quantity)
    {
        var item = await _context.InventoryItems.FirstOrDefaultAsync(i => i.ProductId == productId);
        if (item is null) return null;

        if (item.QuantityOnHand < quantity)
            throw new InvalidOperationException($"Insufficient stock for product {productId}. Available: {item.QuantityOnHand}");

        item.QuantityOnHand -= quantity;
        await _context.SaveChangesAsync();
        return item;
    }

    /// <summary>
    /// Creates a new inventory record or updates an existing one based on the product identifier.
    /// If a record with the same <see cref="InventoryItem.ProductId"/> already exists, its fields are overwritten;
    /// otherwise a new record is inserted.
    /// </summary>
    /// <param name="inventoryItem">The inventory item data to persist.</param>
    /// <returns>The persisted <see cref="InventoryItem"/> (either the updated existing record or the newly created one).</returns>
    public async Task<InventoryItem> CreateOrUpdateAsync(InventoryItem inventoryItem)
    {
        var existing = await _context.InventoryItems.FirstOrDefaultAsync(i => i.ProductId == inventoryItem.ProductId);
        if (existing is not null)
        {
            existing.ProductName = inventoryItem.ProductName;
            existing.ProductSku = inventoryItem.ProductSku;
            existing.QuantityOnHand = inventoryItem.QuantityOnHand;
            existing.ReorderLevel = inventoryItem.ReorderLevel;
            existing.WarehouseLocation = inventoryItem.WarehouseLocation;
            existing.LastRestocked = inventoryItem.LastRestocked;
        }
        else
        {
            _context.InventoryItems.Add(inventoryItem);
        }
        await _context.SaveChangesAsync();
        return existing ?? inventoryItem;
    }
}
