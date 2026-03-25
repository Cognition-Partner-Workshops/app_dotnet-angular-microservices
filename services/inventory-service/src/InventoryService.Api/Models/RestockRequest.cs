namespace InventoryService.Api.Models;

/// <summary>Request payload for restocking inventory.</summary>
/// <param name="Quantity">Number of units to add to current stock.</param>
public record RestockRequest(int Quantity);
