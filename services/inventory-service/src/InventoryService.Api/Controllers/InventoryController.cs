using Microsoft.AspNetCore.Mvc;
using InventoryService.Api.Models;
using InventoryService.Api.Services;

namespace InventoryService.Api.Controllers;

/// <summary>
/// Manages warehouse inventory — stock queries, restocking, reservations, and low-stock alerts.
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

    /// <summary>Returns inventory items whose quantity on hand is at or below the reorder level.</summary>
    /// <response code="200">List of low-stock inventory items.</response>
    [HttpGet("low-stock")]
    [ProducesResponseType(typeof(List<InventoryItem>), StatusCodes.Status200OK)]
    public async Task<IActionResult> GetLowStock() => Ok(await _inventoryService.GetLowStockItemsAsync());

    /// <summary>Atomically checks availability and reserves stock for multiple products.</summary>
    /// <param name="request">Reservation request containing product/quantity pairs.</param>
    /// <response code="200">Reservation succeeded — includes reserved quantities and remaining stock.</response>
    /// <response code="400">Reservation failed — insufficient stock or unknown product.</response>
    [HttpPost("check-and-reserve")]
    [ProducesResponseType(typeof(StockReservationResponse), StatusCodes.Status200OK)]
    [ProducesResponseType(typeof(StockReservationResponse), StatusCodes.Status400BadRequest)]
    public async Task<IActionResult> CheckAndReserve([FromBody] StockReservationRequest request)
    {
        var response = await _inventoryService.CheckAndReserveStockAsync(request);
        return response.Success ? Ok(response) : BadRequest(response);
    }
}
