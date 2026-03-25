using Microsoft.AspNetCore.Mvc;
using InventoryService.Api.Models;
using InventoryService.Api.Services;

namespace InventoryService.Api.Controllers;

/// <summary>
/// Manages warehouse inventory — stock queries, restocking, deductions, and low-stock alerts.
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

    /// <summary>Returns every inventory record.</summary>
    /// <response code="200">List of all inventory items.</response>
    [HttpGet]
    [ProducesResponseType(typeof(List<InventoryItem>), StatusCodes.Status200OK)]
    public async Task<IActionResult> GetAll() => Ok(await _inventoryService.GetAllInventoryAsync());

    /// <summary>Returns the inventory record for a specific product.</summary>
    /// <param name="productId">The product identifier.</param>
    /// <response code="200">The inventory item.</response>
    /// <response code="404">No inventory record exists for this product.</response>
    [HttpGet("product/{productId}")]
    [ProducesResponseType(typeof(InventoryItem), StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<IActionResult> GetByProduct(int productId)
    {
        var item = await _inventoryService.GetInventoryByProductIdAsync(productId);
        return item is null ? NotFound() : Ok(item);
    }

    /// <summary>Adds stock to a product's inventory.</summary>
    /// <param name="productId">The product identifier.</param>
    /// <param name="request">Restock details including quantity to add.</param>
    /// <response code="200">Updated inventory item after restocking.</response>
    /// <response code="404">No inventory record exists for this product.</response>
    [HttpPost("product/{productId}/restock")]
    [ProducesResponseType(typeof(InventoryItem), StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<IActionResult> Restock(int productId, [FromBody] RestockRequest request)
    {
        try
        {
            var item = await _inventoryService.RestockAsync(productId, request.Quantity);
            return Ok(item);
        }
        catch (ArgumentException ex)
        {
            return NotFound(new { error = ex.Message });
        }
    }

    /// <summary>Deducts stock from a product's inventory (called during order fulfillment).</summary>
    /// <param name="productId">The product identifier.</param>
    /// <param name="request">Deduction details including quantity to remove.</param>
    /// <response code="200">Updated inventory item after deduction.</response>
    /// <response code="404">No inventory record exists for this product.</response>
    /// <response code="409">Insufficient stock available for the requested deduction.</response>
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

    /// <summary>Returns inventory items whose quantity on hand is at or below the reorder level.</summary>
    /// <response code="200">List of low-stock inventory items.</response>
    [HttpGet("low-stock")]
    [ProducesResponseType(typeof(List<InventoryItem>), StatusCodes.Status200OK)]
    public async Task<IActionResult> GetLowStock() => Ok(await _inventoryService.GetLowStockItemsAsync());
}

/// <summary>Request payload for restocking inventory.</summary>
/// <param name="Quantity">Number of units to add to current stock.</param>
public record RestockRequest(int Quantity);

/// <summary>Request payload for deducting inventory.</summary>
/// <param name="Quantity">Number of units to remove from current stock.</param>
public record DeductRequest(int Quantity);

/// <summary>Request payload for reserving inventory.</summary>
/// <param name="Quantity">Number of units to reserve.</param>
public record ReserveRequest(int Quantity);
