using Microsoft.AspNetCore.Mvc;
using InventoryService.Api.Services;

namespace InventoryService.Api.Controllers;

/// <summary>
/// REST API controller for managing product inventory.
/// Provides endpoints for querying stock levels, restocking, deducting stock, and identifying low-stock items.
/// </summary>
[ApiController]
[Route("api/[controller]")]
public class InventoryController : ControllerBase
{
    private readonly InventoryItemService _inventoryService;

    /// <summary>
    /// Initializes a new instance of <see cref="InventoryController"/>.
    /// </summary>
    /// <param name="inventoryService">The inventory business-logic service.</param>
    public InventoryController(InventoryItemService inventoryService)
    {
        _inventoryService = inventoryService;
    }

    /// <summary>
    /// Retrieves every inventory item in the system.
    /// </summary>
    /// <returns>A list of all <see cref="Api.Models.InventoryItem"/> records.</returns>
    [HttpGet]
    public async Task<IActionResult> GetAll() => Ok(await _inventoryService.GetAllInventoryAsync());

    /// <summary>
    /// Retrieves the inventory record for a specific product.
    /// </summary>
    /// <param name="productId">The unique product identifier.</param>
    /// <returns>The matching inventory item, or 404 if no record exists for the given product.</returns>
    [HttpGet("product/{productId}")]
    public async Task<IActionResult> GetByProduct(int productId)
    {
        var item = await _inventoryService.GetInventoryByProductIdAsync(productId);
        return item is null ? NotFound() : Ok(item);
    }

    /// <summary>
    /// Adds stock to an existing product's inventory.
    /// </summary>
    /// <param name="productId">The unique product identifier to restock.</param>
    /// <param name="request">The restock payload containing the quantity to add.</param>
    /// <returns>The updated inventory item, or 404 if the product does not exist.</returns>
    [HttpPost("product/{productId}/restock")]
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

    /// <summary>
    /// Retrieves all inventory items whose quantity on hand is at or below their reorder level.
    /// </summary>
    /// <returns>A list of low-stock inventory items.</returns>
    [HttpGet("low-stock")]
    public async Task<IActionResult> GetLowStock() => Ok(await _inventoryService.GetLowStockItemsAsync());

    /// <summary>
    /// Deducts a specified quantity from a product's stock.
    /// </summary>
    /// <param name="productId">The unique product identifier to deduct from.</param>
    /// <param name="request">The deduction payload containing the quantity to remove.</param>
    /// <returns>
    /// The updated inventory item on success, 404 if the product is not found,
    /// or 400 if there is insufficient stock.
    /// </returns>
    [HttpPost("product/{productId}/deduct")]
    public async Task<IActionResult> DeductStock(int productId, [FromBody] DeductStockRequest request)
    {
        try
        {
            var item = await _inventoryService.DeductStockAsync(productId, request.Quantity);
            return item is null ? NotFound() : Ok(item);
        }
        catch (InvalidOperationException ex)
        {
            return BadRequest(new { error = ex.Message });
        }
    }
}

/// <summary>
/// Request body for the restock endpoint.
/// </summary>
/// <param name="Quantity">The number of units to add to the current stock.</param>
public record RestockRequest(int Quantity);

/// <summary>
/// Request body for the deduct-stock endpoint.
/// </summary>
/// <param name="Quantity">The number of units to remove from the current stock.</param>
public record DeductStockRequest(int Quantity);
