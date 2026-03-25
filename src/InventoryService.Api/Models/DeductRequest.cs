namespace InventoryService.Api.Models;

/// <summary>
/// Request payload for deducting stock from an inventory item.
/// </summary>
/// <param name="Quantity">The number of units to deduct from stock.</param>
public record DeductRequest(int Quantity);
