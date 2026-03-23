using Microsoft.EntityFrameworkCore;
using Xunit;
using InventoryService.Api.Data;
using InventoryService.Api.Models;
using InventoryService.Api.Services;

namespace InventoryService.Api.Tests;

/// <summary>
/// Unit tests for <see cref="InventoryItemService"/>.
///
/// Each test uses a unique in-memory database (via <see cref="CreateContext"/>)
/// to ensure complete isolation between test cases. The seed data contains
/// two products: "Widget A" (50 on hand) and "Widget B" (5 on hand, below
/// the reorder level of 10).
/// </summary>
public class InventoryServiceTests
{
    /// <summary>
    /// Creates a fresh <see cref="InventoryDbContext"/> backed by a unique
    /// in-memory database and seeds it with two inventory items.
    /// </summary>
    /// <returns>A disposable <see cref="InventoryDbContext"/> ready for testing.</returns>
    private InventoryDbContext CreateContext()
    {
        var options = new DbContextOptionsBuilder<InventoryDbContext>()
            .UseInMemoryDatabase(databaseName: Guid.NewGuid().ToString())
            .Options;
        var context = new InventoryDbContext(options);
        context.InventoryItems.AddRange(
            new InventoryItem { Id = 1, ProductId = 1, ProductName = "Widget A", ProductSku = "WGT-001", QuantityOnHand = 50, ReorderLevel = 10, WarehouseLocation = "A-01" },
            new InventoryItem { Id = 2, ProductId = 2, ProductName = "Widget B", ProductSku = "WGT-002", QuantityOnHand = 5, ReorderLevel = 10, WarehouseLocation = "A-02" }
        );
        context.SaveChanges();
        return context;
    }

    /// <summary>
    /// Verifies that <see cref="InventoryItemService.GetAllInventoryAsync"/>
    /// returns every seeded inventory item.
    /// </summary>
    [Fact]
    public async Task GetAllInventory_ReturnsAllItems()
    {
        using var context = CreateContext();
        var service = new InventoryItemService(context);
        var result = await service.GetAllInventoryAsync();
        Assert.Equal(2, result.Count);
    }

    /// <summary>
    /// Verifies that looking up an existing product ID returns the correct item.
    /// </summary>
    [Fact]
    public async Task GetByProductId_ReturnsCorrectItem()
    {
        using var context = CreateContext();
        var service = new InventoryItemService(context);
        var result = await service.GetInventoryByProductIdAsync(1);
        Assert.NotNull(result);
        Assert.Equal("Widget A", result.ProductName);
    }

    /// <summary>
    /// Verifies that looking up a non-existent product ID returns <c>null</c>.
    /// </summary>
    [Fact]
    public async Task GetByProductId_ReturnsNull_WhenNotFound()
    {
        using var context = CreateContext();
        var service = new InventoryItemService(context);
        var result = await service.GetInventoryByProductIdAsync(999);
        Assert.Null(result);
    }

    /// <summary>
    /// Verifies that restocking a product increases its <c>QuantityOnHand</c>
    /// by the requested amount.
    /// </summary>
    [Fact]
    public async Task Restock_IncreasesQuantity()
    {
        using var context = CreateContext();
        var service = new InventoryItemService(context);
        var result = await service.RestockAsync(1, 25);
        Assert.Equal(75, result.QuantityOnHand);
    }

    /// <summary>
    /// Verifies that restocking a non-existent product throws
    /// <see cref="ArgumentException"/>.
    /// </summary>
    [Fact]
    public async Task Restock_ThrowsForInvalidProduct()
    {
        using var context = CreateContext();
        var service = new InventoryItemService(context);
        await Assert.ThrowsAsync<ArgumentException>(() => service.RestockAsync(999, 10));
    }

    /// <summary>
    /// Verifies that <see cref="InventoryItemService.GetLowStockItemsAsync"/>
    /// returns only items at or below their reorder level (Widget B = 5 &lt;= 10).
    /// </summary>
    [Fact]
    public async Task GetLowStock_ReturnsItemsBelowReorderLevel()
    {
        using var context = CreateContext();
        var service = new InventoryItemService(context);
        var result = await service.GetLowStockItemsAsync();
        Assert.Single(result);
        Assert.Equal("Widget B", result[0].ProductName);
    }

    /// <summary>
    /// Verifies that deducting stock decreases <c>QuantityOnHand</c> by
    /// the requested amount when sufficient stock is available.
    /// </summary>
    [Fact]
    public async Task DeductStock_DecreasesQuantity()
    {
        using var context = CreateContext();
        var service = new InventoryItemService(context);
        var result = await service.DeductStockAsync(1, 10);
        Assert.NotNull(result);
        Assert.Equal(40, result.QuantityOnHand);
    }

    /// <summary>
    /// Verifies that deducting more stock than available throws
    /// <see cref="InvalidOperationException"/>.
    /// </summary>
    [Fact]
    public async Task DeductStock_ThrowsForInsufficientStock()
    {
        using var context = CreateContext();
        var service = new InventoryItemService(context);
        await Assert.ThrowsAsync<InvalidOperationException>(() => service.DeductStockAsync(2, 100));
    }

    /// <summary>
    /// Verifies that deducting stock for a non-existent product returns <c>null</c>
    /// instead of throwing an exception.
    /// </summary>
    [Fact]
    public async Task DeductStock_ReturnsNull_WhenProductNotFound()
    {
        using var context = CreateContext();
        var service = new InventoryItemService(context);
        var result = await service.DeductStockAsync(999, 10);
        Assert.Null(result);
    }
}
