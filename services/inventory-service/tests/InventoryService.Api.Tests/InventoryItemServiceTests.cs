using Microsoft.EntityFrameworkCore;
using InventoryService.Api.Data;
using InventoryService.Api.Models;
using InventoryService.Api.Services;

namespace InventoryService.Api.Tests;

public class InventoryItemServiceTests
{
    private static InventoryDbContext CreateContext(string dbName)
    {
        var options = new DbContextOptionsBuilder<InventoryDbContext>()
            .UseInMemoryDatabase(databaseName: dbName)
            .Options;
        return new InventoryDbContext(options);
    }

    private static void SeedTestData(InventoryDbContext context)
    {
        context.InventoryItems.AddRange(
            new InventoryItem { Id = 1, ProductId = 1, ProductName = "Widget A", QuantityOnHand = 50, ReorderLevel = 10, WarehouseLocation = "A-01" },
            new InventoryItem { Id = 2, ProductId = 2, ProductName = "Widget B", QuantityOnHand = 5, ReorderLevel = 10, WarehouseLocation = "A-02" },
            new InventoryItem { Id = 3, ProductId = 3, ProductName = "Gadget X", QuantityOnHand = 150, ReorderLevel = 10, WarehouseLocation = "A-03" }
        );
        context.SaveChanges();
    }

    [Fact]
    public async Task GetAllInventoryAsync_ReturnsAllItems()
    {
        using var context = CreateContext(nameof(GetAllInventoryAsync_ReturnsAllItems));
        SeedTestData(context);
        var service = new InventoryItemService(context);

        var result = await service.GetAllInventoryAsync();

        Assert.Equal(3, result.Count);
    }

    [Fact]
    public async Task GetInventoryByProductIdAsync_ReturnsCorrectItem()
    {
        using var context = CreateContext(nameof(GetInventoryByProductIdAsync_ReturnsCorrectItem));
        SeedTestData(context);
        var service = new InventoryItemService(context);

        var result = await service.GetInventoryByProductIdAsync(1);

        Assert.NotNull(result);
        Assert.Equal("Widget A", result.ProductName);
        Assert.Equal(50, result.QuantityOnHand);
    }

    [Fact]
    public async Task GetInventoryByProductIdAsync_ReturnsNull_WhenNotFound()
    {
        using var context = CreateContext(nameof(GetInventoryByProductIdAsync_ReturnsNull_WhenNotFound));
        SeedTestData(context);
        var service = new InventoryItemService(context);

        var result = await service.GetInventoryByProductIdAsync(999);

        Assert.Null(result);
    }

    [Fact]
    public async Task RestockAsync_IncreasesQuantity()
    {
        using var context = CreateContext(nameof(RestockAsync_IncreasesQuantity));
        SeedTestData(context);
        var service = new InventoryItemService(context);

        var result = await service.RestockAsync(1, 25);

        Assert.Equal(75, result.QuantityOnHand);
    }

    [Fact]
    public async Task RestockAsync_ThrowsForUnknownProduct()
    {
        using var context = CreateContext(nameof(RestockAsync_ThrowsForUnknownProduct));
        SeedTestData(context);
        var service = new InventoryItemService(context);

        await Assert.ThrowsAsync<ArgumentException>(() => service.RestockAsync(999, 10));
    }

    [Fact]
    public async Task GetLowStockItemsAsync_ReturnsOnlyLowStock()
    {
        using var context = CreateContext(nameof(GetLowStockItemsAsync_ReturnsOnlyLowStock));
        SeedTestData(context);
        var service = new InventoryItemService(context);

        var result = await service.GetLowStockItemsAsync();

        Assert.Single(result);
        Assert.Equal("Widget B", result[0].ProductName);
        Assert.Equal(5, result[0].QuantityOnHand);
    }
}
