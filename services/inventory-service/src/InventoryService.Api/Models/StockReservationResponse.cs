namespace InventoryService.Api.Models;

/// <summary>Result of a stock reservation attempt.</summary>
public class StockReservationResponse
{
    /// <summary>Whether all items were successfully reserved.</summary>
    public bool Success { get; set; }

    /// <summary>Human-readable error message when <see cref="Success"/> is false.</summary>
    public string? Error { get; set; }

    /// <summary>Details for each reserved line item.</summary>
    public List<ReservedItem> ReservedItems { get; set; } = new();
}

/// <summary>Confirmation details for a single reserved line item.</summary>
public class ReservedItem
{
    /// <summary>Product identifier.</summary>
    public int ProductId { get; set; }

    /// <summary>Number of units that were reserved.</summary>
    public int QuantityReserved { get; set; }

    /// <summary>Stock remaining after the reservation.</summary>
    public int RemainingStock { get; set; }
}
