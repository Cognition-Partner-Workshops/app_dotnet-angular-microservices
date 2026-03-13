using Microsoft.AspNetCore.Mvc;
using InventoryService.Api.DTOs;
using InventoryService.Api.Services;

namespace InventoryService.Api.Controllers;

[ApiController]
[Route("api/[controller]")]
public class InventoryController : ControllerBase
{
    private readonly InventoryItemService _inventoryService;

    public InventoryController(InventoryItemService inventoryService)
    {
        _inventoryService = inventoryService;
    }

    [HttpGet]
    public async Task<IActionResult> GetAll() =>
        Ok(await _inventoryService.GetAllInventoryAsync());

    [HttpGet("product/{productId}")]
    public async Task<IActionResult> GetByProduct(int productId)
    {
        var item = await _inventoryService.GetInventoryByProductIdAsync(productId);
        return item is null ? NotFound() : Ok(item);
    }

    [HttpPost("product/{productId}/restock")]
    public async Task<IActionResult> Restock(int productId, [FromBody] RestockRequest request)
    {
        var item = await _inventoryService.RestockAsync(productId, request.Quantity);
        return Ok(item);
    }

    [HttpGet("low-stock")]
    public async Task<IActionResult> GetLowStock() =>
        Ok(await _inventoryService.GetLowStockItemsAsync());

    [HttpPost("check-stock")]
    public async Task<IActionResult> CheckStock([FromBody] CheckStockRequest request)
    {
        var result = await _inventoryService.CheckStockAsync(request.ProductId, request.Quantity);
        return Ok(result);
    }

    [HttpPost("deduct-stock")]
    public async Task<IActionResult> DeductStock([FromBody] DeductStockRequest request)
    {
        try
        {
            var item = await _inventoryService.DeductStockAsync(request.ProductId, request.Quantity);
            return Ok(item);
        }
        catch (InvalidOperationException ex)
        {
            return BadRequest(new { error = ex.Message });
        }
        catch (ArgumentException ex)
        {
            return NotFound(new { error = ex.Message });
        }
    }
}
