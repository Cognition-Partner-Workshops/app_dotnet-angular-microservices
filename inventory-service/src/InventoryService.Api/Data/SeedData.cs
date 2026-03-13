using InventoryService.Api.Models;

namespace InventoryService.Api.Data;

/// <summary>
/// Provides initial seed data for the inventory database.
/// Populates the database with sample products on first run when no inventory records exist.
/// </summary>
public static class SeedData
{
    /// <summary>
    /// Seeds the database with a default set of inventory items if the table is empty.
    /// Ensures the database schema is created before attempting to insert data.
    /// </summary>
    /// <param name="context">The <see cref="InventoryDbContext"/> used to access and populate the database.</param>
    public static void Initialize(InventoryDbContext context)
    {
        context.Database.EnsureCreated();

        if (context.InventoryItems.Any()) return;

        var inventoryItems = new[]
        {
            new InventoryItem { ProductId = 1, ProductName = "Widget A", ProductSku = "WGT-001", QuantityOnHand = 50, ReorderLevel = 10, WarehouseLocation = "A-01" },
            new InventoryItem { ProductId = 2, ProductName = "Widget B", ProductSku = "WGT-002", QuantityOnHand = 100, ReorderLevel = 10, WarehouseLocation = "A-02" },
            new InventoryItem { ProductId = 3, ProductName = "Gadget X", ProductSku = "GDG-001", QuantityOnHand = 150, ReorderLevel = 10, WarehouseLocation = "A-03" },
            new InventoryItem { ProductId = 4, ProductName = "Gadget Y", ProductSku = "GDG-002", QuantityOnHand = 200, ReorderLevel = 10, WarehouseLocation = "A-04" },
            new InventoryItem { ProductId = 5, ProductName = "Thingamajig", ProductSku = "THG-001", QuantityOnHand = 250, ReorderLevel = 10, WarehouseLocation = "A-05" },
        };

        context.InventoryItems.AddRange(inventoryItems);
        context.SaveChanges();
    }
}
