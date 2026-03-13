using Microsoft.EntityFrameworkCore;
using InventoryService.Api.Data;
using InventoryService.Api.Models;
using InventoryService.Api.Services;
using Xunit;

namespace InventoryService.Api.Tests;

public class InventoryServiceTests
{
    private static InventoryDbContext CreateContext()
    {
        var options = new DbContextOptionsBuilder<InventoryDbContext>()
            .UseInMemoryDatabase(databaseName: Guid.NewGuid().ToString())
            .Options;
        var context = new InventoryDbContext(options);

        var product = new Product { Id = 1, Name = "Test Widget", Sku = "TST-001", Price = 9.99m, Category = "Test" };
        context.Products.Add(product);
        context.InventoryItems.Add(new InventoryItem
        {
            Id = 1,
            ProductId = 1,
            Product = product,
            QuantityOnHand = 100,
            ReorderLevel = 10,
            WarehouseLocation = "A-01"
        });
        context.SaveChanges();
        return context;
    }

    [Fact]
    public async Task GetAllInventoryAsync_ReturnsAllItems()
    {
        using var context = CreateContext();
        var service = new InventoryItemService(context);

        var result = await service.GetAllInventoryAsync();

        Assert.Single(result);
        Assert.Equal("Test Widget", result[0].ProductName);
    }

    [Fact]
    public async Task GetInventoryByProductIdAsync_ReturnsItem()
    {
        using var context = CreateContext();
        var service = new InventoryItemService(context);

        var result = await service.GetInventoryByProductIdAsync(1);

        Assert.NotNull(result);
        Assert.Equal(100, result.QuantityOnHand);
    }

    [Fact]
    public async Task GetInventoryByProductIdAsync_ReturnsNull_WhenNotFound()
    {
        using var context = CreateContext();
        var service = new InventoryItemService(context);

        var result = await service.GetInventoryByProductIdAsync(999);

        Assert.Null(result);
    }

    [Fact]
    public async Task RestockAsync_IncreasesQuantity()
    {
        using var context = CreateContext();
        var service = new InventoryItemService(context);

        var result = await service.RestockAsync(1, 50);

        Assert.Equal(150, result.QuantityOnHand);
    }

    [Fact]
    public async Task GetLowStockItemsAsync_ReturnsEmpty_WhenStockSufficient()
    {
        using var context = CreateContext();
        var service = new InventoryItemService(context);

        var result = await service.GetLowStockItemsAsync();

        Assert.Empty(result);
    }

    [Fact]
    public async Task CheckStockAsync_ReturnsTrue_WhenStockAvailable()
    {
        using var context = CreateContext();
        var service = new InventoryItemService(context);

        var result = await service.CheckStockAsync(1, 50);

        Assert.True(result.InStock);
        Assert.Equal(100, result.AvailableQuantity);
    }

    [Fact]
    public async Task CheckStockAsync_ReturnsFalse_WhenInsufficientStock()
    {
        using var context = CreateContext();
        var service = new InventoryItemService(context);

        var result = await service.CheckStockAsync(1, 200);

        Assert.False(result.InStock);
    }

    [Fact]
    public async Task DeductStockAsync_DecreasesQuantity()
    {
        using var context = CreateContext();
        var service = new InventoryItemService(context);

        var result = await service.DeductStockAsync(1, 30);

        Assert.Equal(70, result.QuantityOnHand);
    }

    [Fact]
    public async Task DeductStockAsync_ThrowsWhenInsufficient()
    {
        using var context = CreateContext();
        var service = new InventoryItemService(context);

        await Assert.ThrowsAsync<InvalidOperationException>(
            () => service.DeductStockAsync(1, 200));
    }
}
