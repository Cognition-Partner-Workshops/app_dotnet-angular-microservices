namespace InventoryService.Api.Models;

/// <summary>
/// Represents a single inventory record tracking stock levels for a product in the warehouse.
/// </summary>
public class InventoryItem
{
    /// <summary>
    /// Gets or sets the unique identifier for this inventory record.
    /// </summary>
    public int Id { get; set; }

    /// <summary>
    /// Gets or sets the identifier of the product this inventory record tracks.
    /// Must be unique across all inventory items.
    /// </summary>
    public int ProductId { get; set; }

    /// <summary>
    /// Gets or sets the display name of the product.
    /// </summary>
    public string ProductName { get; set; } = string.Empty;

    /// <summary>
    /// Gets or sets the stock-keeping unit code used to identify the product in warehouse operations.
    /// </summary>
    public string ProductSku { get; set; } = string.Empty;

    /// <summary>
    /// Gets or sets the current number of units available in stock.
    /// </summary>
    public int QuantityOnHand { get; set; }

    /// <summary>
    /// Gets or sets the minimum stock threshold. Items at or below this level are flagged as low stock.
    /// Defaults to 10 units.
    /// </summary>
    public int ReorderLevel { get; set; } = 10;

    /// <summary>
    /// Gets or sets the warehouse bin or aisle location for this product (e.g., "A-01").
    /// </summary>
    public string WarehouseLocation { get; set; } = string.Empty;

    /// <summary>
    /// Gets or sets the UTC timestamp of the most recent restock operation for this product.
    /// </summary>
    public DateTime LastRestocked { get; set; } = DateTime.UtcNow;
}
