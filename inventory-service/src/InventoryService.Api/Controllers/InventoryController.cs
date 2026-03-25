using Microsoft.AspNetCore.Mvc;
using InventoryService.Api.Services;

namespace InventoryService.Api.Controllers;

[ApiController]
[Route("api/[controller]")]
public class InventoryController : ControllerBase
{
    private readonly InventoryManager _inventoryManager;

    public InventoryController(InventoryManager inventoryManager)
    {
        _inventoryManager = inventoryManager;
    }

    [HttpGet]
    public async Task<IActionResult> GetAll() => Ok(await _inventoryManager.GetAllInventoryAsync());

    [HttpGet("product/{productId}")]
    public async Task<IActionResult> GetByProduct(int productId)
    {
        var item = await _inventoryManager.GetInventoryByProductIdAsync(productId);
        return item is null ? NotFound() : Ok(item);
    }

    [HttpPost("product/{productId}/restock")]
    public async Task<IActionResult> Restock(int productId, [FromBody] RestockRequest request)
    {
        try
        {
            var item = await _inventoryManager.RestockAsync(productId, request.Quantity);
            return Ok(item);
        }
        catch (ArgumentException ex)
        {
            return NotFound(new { error = ex.Message });
        }
    }

    [HttpGet("low-stock")]
    public async Task<IActionResult> GetLowStock() => Ok(await _inventoryManager.GetLowStockItemsAsync());

    [HttpPost("product/{productId}/reserve")]
    public async Task<IActionResult> ReserveStock(int productId, [FromBody] ReserveRequest request)
    {
        try
        {
            var item = await _inventoryManager.ReserveStockAsync(productId, request.Quantity);
            return Ok(item);
        }
        catch (ArgumentException ex)
        {
            return NotFound(new { error = ex.Message });
        }
        catch (InvalidOperationException ex)
        {
            return Conflict(new { error = ex.Message });
        }
    }

    [HttpPost("product/{productId}/release")]
    public async Task<IActionResult> ReleaseStock(int productId, [FromBody] ReleaseRequest request)
    {
        try
        {
            var item = await _inventoryManager.ReleaseStockAsync(productId, request.Quantity);
            return Ok(item);
        }
        catch (ArgumentException ex)
        {
            return NotFound(new { error = ex.Message });
        }
    }
}

public record RestockRequest(int Quantity);
public record ReserveRequest(int Quantity);
public record ReleaseRequest(int Quantity);
