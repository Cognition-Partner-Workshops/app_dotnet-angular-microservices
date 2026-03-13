namespace InventoryService.Api.DTOs;

public record InventoryItemDto(
    int Id,
    int ProductId,
    string ProductName,
    string ProductSku,
    int QuantityOnHand,
    int ReorderLevel,
    string WarehouseLocation,
    DateTime LastRestocked
);

public record RestockRequest(int Quantity);

public record CreateInventoryItemRequest(
    int ProductId,
    string ProductName,
    string ProductSku,
    int QuantityOnHand,
    int ReorderLevel,
    string WarehouseLocation
);

public record CheckStockRequest(int ProductId, int Quantity);

public record CheckStockResponse(bool InStock, int AvailableQuantity);

public record DeductStockRequest(int ProductId, int Quantity);
