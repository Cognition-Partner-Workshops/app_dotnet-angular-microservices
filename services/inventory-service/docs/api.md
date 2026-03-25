# Inventory Service API Reference

Base URL: `http://localhost:5100` (local development)

All endpoints are under the `/api/inventory` route prefix. The service also exposes Swagger UI at `/swagger` when running.

## Authentication

No authentication is currently required. CORS is configured to allow all origins.

## Endpoints

### List All Inventory Items

```
GET /api/inventory
```

Returns all inventory items in the system.

#### Response

**Status:** `200 OK`

```json
[
  {
    "id": 1,
    "productId": 1,
    "productName": "Widget A",
    "quantityOnHand": 50,
    "reorderLevel": 10,
    "warehouseLocation": "A-01",
    "lastRestocked": "2026-03-25T13:46:57.403Z"
  },
  {
    "id": 2,
    "productId": 2,
    "productName": "Widget B",
    "quantityOnHand": 100,
    "reorderLevel": 10,
    "warehouseLocation": "A-02",
    "lastRestocked": "2026-03-25T13:46:57.404Z"
  }
]
```

#### cURL Example

```bash
curl -s http://localhost:5100/api/inventory | python3 -m json.tool
```

---

### Get Inventory by Product ID

```
GET /api/inventory/product/{productId}
```

Returns the inventory record for a specific product.

#### Path Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `productId` | int | Yes | The product identifier |

#### Response

**Status:** `200 OK`

```json
{
  "id": 1,
  "productId": 1,
  "productName": "Widget A",
  "quantityOnHand": 50,
  "reorderLevel": 10,
  "warehouseLocation": "A-01",
  "lastRestocked": "2026-03-25T13:46:57.403Z"
}
```

**Status:** `404 Not Found` — No inventory record exists for the given product ID.

#### cURL Example

```bash
curl -s http://localhost:5100/api/inventory/product/1 | python3 -m json.tool
```

---

### Restock a Product

```
POST /api/inventory/product/{productId}/restock
```

Adds quantity to an existing inventory item and updates the `lastRestocked` timestamp.

#### Path Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `productId` | int | Yes | The product identifier |

#### Request Body

```json
{
  "quantity": 25
}
```

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `quantity` | int | Yes | Number of units to add to current stock |

#### Response

**Status:** `200 OK` — Returns the updated inventory item.

```json
{
  "id": 1,
  "productId": 1,
  "productName": "Widget A",
  "quantityOnHand": 75,
  "reorderLevel": 10,
  "warehouseLocation": "A-01",
  "lastRestocked": "2026-03-25T14:00:00.000Z"
}
```

**Status:** `500 Internal Server Error` — No inventory record exists for the given product ID (throws `ArgumentException`).

#### cURL Example

```bash
curl -s -X POST http://localhost:5100/api/inventory/product/1/restock \
  -H "Content-Type: application/json" \
  -d '{"quantity": 25}' | python3 -m json.tool
```

---

### Check Stock Availability

```
GET /api/inventory/product/{productId}/check
```

Checks whether sufficient stock is available for a given product and quantity. This is a read-only operation that does not modify inventory.

#### Path Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `productId` | int | Yes | The product identifier |

#### Query Parameters

| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `quantity` | int | No | 1 | The quantity to check availability for |

#### Response

**Status:** `200 OK`

```json
{
  "productId": 1,
  "quantity": 5,
  "available": true
}
```

The `available` field is `true` if `quantityOnHand >= quantity`, and `false` otherwise. If no inventory record exists for the product, `available` is `false`.

#### cURL Example

```bash
curl -s "http://localhost:5100/api/inventory/product/1/check?quantity=5"
```

---

### Deduct Stock

```
POST /api/inventory/product/{productId}/deduct
```

Deducts a specified quantity from the product's stock. This endpoint is primarily used by the OrderManager monolith during order creation.

#### Path Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `productId` | int | Yes | The product identifier |

#### Request Body

```json
{
  "quantity": 2
}
```

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `quantity` | int | Yes | Number of units to deduct from current stock |

#### Response

**Status:** `200 OK` — Returns the updated inventory item after deduction.

```json
{
  "id": 1,
  "productId": 1,
  "productName": "Widget A",
  "quantityOnHand": 48,
  "reorderLevel": 10,
  "warehouseLocation": "A-01",
  "lastRestocked": "2026-03-25T13:46:57.403Z"
}
```

**Status:** `409 Conflict` — Insufficient stock available.

```json
{
  "error": "Insufficient stock for product 1. Available: 48"
}
```

**Status:** `500 Internal Server Error` — No inventory record exists for the given product ID.

#### cURL Example

```bash
curl -s -X POST http://localhost:5100/api/inventory/product/1/deduct \
  -H "Content-Type: application/json" \
  -d '{"quantity": 2}' | python3 -m json.tool
```

---

### Get Low Stock Items

```
GET /api/inventory/low-stock
```

Returns all inventory items where `quantityOnHand <= reorderLevel`. Useful for generating restock alerts.

#### Response

**Status:** `200 OK`

```json
[
  {
    "id": 1,
    "productId": 1,
    "productName": "Widget A",
    "quantityOnHand": 8,
    "reorderLevel": 10,
    "warehouseLocation": "A-01",
    "lastRestocked": "2026-03-25T13:46:57.403Z"
  }
]
```

Returns an empty array `[]` if all items are above their reorder level.

#### cURL Example

```bash
curl -s http://localhost:5100/api/inventory/low-stock | python3 -m json.tool
```

---

### Health Check

```
GET /health
```

Returns the health status of the service.

#### Response

**Status:** `200 OK`

```
Healthy
```

#### cURL Example

```bash
curl -s http://localhost:5100/health
```

---

## Error Handling

| HTTP Status | Meaning | When |
|-------------|---------|------|
| `200 OK` | Success | Request completed successfully |
| `404 Not Found` | Resource not found | No inventory record for the given product ID (GET by product) |
| `409 Conflict` | Business rule violation | Insufficient stock for deduction |
| `500 Internal Server Error` | Server error | Unexpected errors (e.g., missing product for restock/deduct) |

## Data Types

### InventoryItem

| Field | Type | Description |
|-------|------|-------------|
| `id` | int | Auto-generated unique identifier |
| `productId` | int | Product identifier (matches monolith's Product.Id) |
| `productName` | string | Denormalized product name |
| `quantityOnHand` | int | Current stock quantity |
| `reorderLevel` | int | Low-stock threshold |
| `warehouseLocation` | string | Physical location code |
| `lastRestocked` | string (ISO 8601) | UTC timestamp of last restock |

### RestockRequest

| Field | Type | Description |
|-------|------|-------------|
| `quantity` | int | Units to add |

### DeductRequest

| Field | Type | Description |
|-------|------|-------------|
| `quantity` | int | Units to deduct |

### StockCheckResponse

| Field | Type | Description |
|-------|------|-------------|
| `productId` | int | The product checked |
| `quantity` | int | The quantity checked |
| `available` | bool | Whether sufficient stock exists |

## Rate Limiting

No rate limiting is currently configured. For production deployments, consider adding rate limiting via an API gateway or middleware.
