using Microsoft.EntityFrameworkCore;
using InventoryService.Api.Data;
using InventoryService.Api.DTOs;
using InventoryService.Api.Models;

namespace InventoryService.Api.Services;

public class InventoryItemService
{
    private readonly InventoryDbContext _context;

    public InventoryItemService(InventoryDbContext context)
    {
        _context = context;
    }

    public async Task<List<InventoryItemDto>> GetAllInventoryAsync()
    {
        return await _context.InventoryItems
            .Include(i => i.Product)
            .Select(i => MapToDto(i))
            .ToListAsync();
    }

    public async Task<InventoryItemDto?> GetInventoryByProductIdAsync(int productId)
    {
        var item = await _context.InventoryItems
            .Include(i => i.Product)
            .FirstOrDefaultAsync(i => i.ProductId == productId);
        return item is null ? null : MapToDto(item);
    }

    public async Task<InventoryItemDto> RestockAsync(int productId, int quantity)
    {
        var item = await _context.InventoryItems
            .Include(i => i.Product)
            .FirstOrDefaultAsync(i => i.ProductId == productId)
            ?? throw new ArgumentException($"No inventory record for product {productId}");
        item.QuantityOnHand += quantity;
        item.LastRestocked = DateTime.UtcNow;
        await _context.SaveChangesAsync();
        return MapToDto(item);
    }

    public async Task<List<InventoryItemDto>> GetLowStockItemsAsync()
    {
        return await _context.InventoryItems
            .Include(i => i.Product)
            .Where(i => i.QuantityOnHand <= i.ReorderLevel)
            .Select(i => MapToDto(i))
            .ToListAsync();
    }

    public async Task<CheckStockResponse> CheckStockAsync(int productId, int quantity)
    {
        var item = await _context.InventoryItems
            .FirstOrDefaultAsync(i => i.ProductId == productId);
        if (item is null)
            return new CheckStockResponse(false, 0);
        return new CheckStockResponse(item.QuantityOnHand >= quantity, item.QuantityOnHand);
    }

    public async Task<InventoryItemDto> DeductStockAsync(int productId, int quantity)
    {
        var item = await _context.InventoryItems
            .Include(i => i.Product)
            .FirstOrDefaultAsync(i => i.ProductId == productId)
            ?? throw new ArgumentException($"No inventory record for product {productId}");

        if (item.QuantityOnHand < quantity)
            throw new InvalidOperationException(
                $"Insufficient stock for product {productId}. Available: {item.QuantityOnHand}");

        item.QuantityOnHand -= quantity;
        await _context.SaveChangesAsync();
        return MapToDto(item);
    }

    private static InventoryItemDto MapToDto(InventoryItem item) => new(
        item.Id,
        item.ProductId,
        item.Product.Name,
        item.Product.Sku,
        item.QuantityOnHand,
        item.ReorderLevel,
        item.WarehouseLocation,
        item.LastRestocked
    );
}
