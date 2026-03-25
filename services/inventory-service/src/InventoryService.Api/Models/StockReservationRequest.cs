namespace InventoryService.Api.Models;

/// <summary>Request to atomically check and reserve stock for one or more products.</summary>
public class StockReservationRequest
{
    /// <summary>List of product/quantity pairs to reserve.</summary>
    public List<StockReservationItem> Items { get; set; } = new();
}

/// <summary>A single line item in a stock reservation request.</summary>
public class StockReservationItem
{
    /// <summary>Product identifier.</summary>
    public int ProductId { get; set; }

    /// <summary>Number of units to reserve.</summary>
    public int Quantity { get; set; }
}
