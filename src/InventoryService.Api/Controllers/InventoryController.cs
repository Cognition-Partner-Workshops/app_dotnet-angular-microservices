using Microsoft.AspNetCore.Mvc;
using InventoryService.Api.Models;
using InventoryService.Api.Services;

namespace InventoryService.Api.Controllers;

/// <summary>
/// Manages inventory stock levels, restocking operations, and low-stock alerts.
/// </summary>
[ApiController]
[Route("api/[controller]")]
[Produces("application/json")]
public class InventoryController : ControllerBase
{
    private readonly InventoryItemService _inventoryService;

    public InventoryController(InventoryItemService inventoryService)
    {
        _inventoryService = inventoryService;
    }

    /// <summary>Get all inventory items.</summary>
    /// <returns>A list of all inventory items with current stock levels.</returns>
    /// <response code="200">Returns the list of inventory items.</response>
    [HttpGet]
    [ProducesResponseType(typeof(List<InventoryItem>), StatusCodes.Status200OK)]
    public async Task<IActionResult> GetAll()
    {
        return Ok(await _inventoryService.GetAllInventoryAsync());
    }

    /// <summary>Get inventory for a specific product.</summary>
    /// <param name="productId">The product ID to look up.</param>
    /// <returns>The inventory item for the specified product.</returns>
    /// <response code="200">Returns the inventory item.</response>
    /// <response code="404">No inventory record found for the given product ID.</response>
    [HttpGet("product/{productId}")]
    [ProducesResponseType(typeof(InventoryItem), StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<IActionResult> GetByProduct(int productId)
    {
        var item = await _inventoryService.GetInventoryByProductIdAsync(productId);
        return item is null ? NotFound() : Ok(item);
    }

    /// <summary>Restock a product by adding quantity.</summary>
    /// <param name="productId">The product ID to restock.</param>
    /// <param name="request">The restock request containing the quantity to add.</param>
    /// <returns>The updated inventory item.</returns>
    /// <response code="200">Successfully restocked. Returns the updated inventory item.</response>
    /// <response code="400">Invalid quantity or no inventory record found.</response>
    [HttpPost("product/{productId}/restock")]
    [ProducesResponseType(typeof(InventoryItem), StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    public async Task<IActionResult> Restock(int productId, [FromBody] RestockRequest request)
    {
        try
        {
            var item = await _inventoryService.RestockAsync(productId, request.Quantity);
            return Ok(item);
        }
        catch (ArgumentException ex)
        {
            return BadRequest(new { error = ex.Message });
        }
    }

    /// <summary>Get all items at or below reorder level.</summary>
    /// <returns>A list of inventory items that need restocking.</returns>
    /// <response code="200">Returns the list of low-stock items.</response>
    [HttpGet("low-stock")]
    [ProducesResponseType(typeof(List<InventoryItem>), StatusCodes.Status200OK)]
    public async Task<IActionResult> GetLowStock()
    {
        return Ok(await _inventoryService.GetLowStockItemsAsync());
    }

    /// <summary>Deduct stock for a product.</summary>
    [HttpPost("product/{productId}/deduct")]
    [ProducesResponseType(typeof(InventoryItem), StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status409Conflict)]
    public async Task<IActionResult> Deduct(int productId, [FromBody] DeductRequest request)
    {
        try
        {
            var item = await _inventoryService.DeductStockAsync(productId, request.Quantity);
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

    /// <summary>Check stock availability for a product.</summary>
    [HttpGet("product/{productId}/check")]
    [ProducesResponseType(StatusCodes.Status200OK)]
    public async Task<IActionResult> CheckStock(int productId, [FromQuery] int quantity)
    {
        var available = await _inventoryService.CheckStockAsync(productId, quantity);
        return Ok(new { productId, quantity, available });
    }

    /// <summary>Reserve stock for an order (used by Order service).</summary>
    [HttpPost("product/{productId}/reserve")]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status409Conflict)]
    public async Task<IActionResult> ReserveStock(int productId, [FromBody] ReserveRequest request)
    {
        var success = await _inventoryService.ReserveStockAsync(productId, request.Quantity);
        if (!success)
            return Conflict(new { error = $"Insufficient stock for product {productId}" });

        return Ok(new { reserved = true, productId, quantity = request.Quantity });
    }
}

public record RestockRequest(int Quantity);
public record DeductRequest(int Quantity);
public record ReserveRequest(int Quantity);
