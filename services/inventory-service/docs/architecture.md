# Inventory Service Architecture

## Overview

The Inventory Service is a standalone microservice decomposed from the OrderManager monolith. It follows a layered architecture pattern and owns its entire data lifecycle independently.

## System Context

```
                                    ┌──────────────────┐
                                    │   Angular 17     │
                                    │   Frontend       │
                                    └────────┬─────────┘
                                             │ HTTP
                                             ▼
┌──────────────────────────────┐    ┌──────────────────┐
│   OrderManager Monolith      │───>│ Inventory Service │
│                              │HTTP│                  │
│  - Orders                    │    │  - Stock Levels  │
│  - Products                  │    │  - Restocking    │
│  - Customers                 │    │  - Low-Stock     │
│  - Inventory (proxy/BFF)     │    │  - Deductions    │
│                              │    │  - Availability  │
│  ┌────────────────────────┐  │    │                  │
│  │ IInventoryServiceClient│──│───>│ /api/inventory/* │
│  │ (HttpClient)           │  │    │                  │
│  └────────────────────────┘  │    │  ┌────────────┐  │
│                              │    │  │  SQLite DB │  │
│  ┌────────────────────────┐  │    │  │ inventory  │  │
│  │   SQLite DB            │  │    │  │   .db      │  │
│  │ orders, products,      │  │    │  └────────────┘  │
│  │ customers              │  │    │                  │
│  └────────────────────────┘  │    └──────────────────┘
└──────────────────────────────┘
```

## Application Layers

```
┌──────────────────────────────────────────────────┐
│                  Controllers                      │
│  InventoryController.cs                          │
│  - Route: /api/inventory                         │
│  - Handles HTTP requests/responses               │
│  - Input validation and error mapping            │
├──────────────────────────────────────────────────┤
│                  Services                         │
│  InventoryBusinessService.cs                     │
│  - Business logic and domain rules               │
│  - Stock validation (sufficient qty checks)      │
│  - Restock timestamp management                  │
├──────────────────────────────────────────────────┤
│                  Data Access                      │
│  InventoryDbContext.cs                           │
│  - EF Core DbContext                             │
│  - Entity configuration (constraints, keys)      │
│  - SQLite provider                               │
├──────────────────────────────────────────────────┤
│                  Models                           │
│  InventoryItem.cs                                │
│  - Domain entity                                 │
│  - Properties: Id, ProductId, ProductName,       │
│    QuantityOnHand, ReorderLevel,                 │
│    WarehouseLocation, LastRestocked              │
└──────────────────────────────────────────────────┘
```

### Controller Layer

The `InventoryController` is the single REST controller. It:
- Maps HTTP routes to service method calls
- Handles HTTP status codes (`404 Not Found`, `409 Conflict`)
- Catches `InvalidOperationException` from the service layer and maps it to `409 Conflict`
- Accepts request DTOs (`RestockRequest`, `DeductRequest`) as C# records

### Service Layer

The `InventoryBusinessService` contains all business logic:
- **GetAllInventoryAsync**: Returns all inventory items
- **GetInventoryByProductIdAsync**: Lookup by product ID
- **RestockAsync**: Adds quantity and updates `LastRestocked` timestamp
- **CheckStockAsync**: Read-only availability check (returns bool)
- **DeductStockAsync**: Validates sufficient stock, deducts, throws on insufficient
- **GetLowStockItemsAsync**: Filters items at or below reorder level

### Data Layer

- **InventoryDbContext**: Single DbSet for `InventoryItem` with entity constraints
- **SeedData**: Static initializer that populates 5 sample items on first startup
- **SQLite**: File-based database (`inventory.db`), created via `EnsureCreated()`

## Dependency Injection

All dependencies are registered in `Program.cs`:

```csharp
// DbContext with SQLite
builder.Services.AddDbContext<InventoryDbContext>(options =>
    options.UseSqlite(connectionString));

// Business service (scoped lifetime)
builder.Services.AddScoped<InventoryBusinessService>();

// Health checks
builder.Services.AddHealthChecks();
```

## Communication Patterns

### Monolith to Inventory Service

The monolith uses two integration patterns:

#### 1. BFF Proxy Pattern
The monolith's `InventoryController` proxies all inventory UI requests to this service:

```
Browser → Monolith /api/inventory → Inventory Service /api/inventory
```

This keeps the Angular frontend pointing at a single backend URL while the inventory data is served by the microservice.

#### 2. Direct Service-to-Service Calls
The monolith's `OrderService` calls this service directly during order creation:

```
OrderService.CreateOrderAsync()
  → IInventoryServiceClient.CheckStockAsync()    → GET  /api/inventory/product/{id}/check
  → IInventoryServiceClient.DeductStockAsync()   → POST /api/inventory/product/{id}/deduct
```

### Error Propagation

| Service Error | HTTP Response | Monolith Handling |
|--------------|---------------|-------------------|
| Product not found | `500` (ArgumentException) | `InvalidOperationException` thrown |
| Insufficient stock | `409 Conflict` | `InvalidOperationException` thrown |
| Service unavailable | Connection refused | `HttpRequestException` propagates |

## Data Ownership

The inventory service owns:
- **InventoryItem** entity and all stock-related data
- **ProductId** is a reference to the monolith's Product table (eventual consistency)
- **ProductName** is denormalized for display purposes (not kept in sync)

The monolith owns:
- **Product** entity (name, price, SKU, category)
- **Customer** entity
- **Order** entity and order items

## Database Schema

```sql
CREATE TABLE "InventoryItems" (
    "Id"                INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "ProductId"         INTEGER NOT NULL,
    "ProductName"       TEXT    NOT NULL,    -- max 200 chars
    "QuantityOnHand"    INTEGER NOT NULL,
    "ReorderLevel"      INTEGER NOT NULL,
    "WarehouseLocation" TEXT    NOT NULL,    -- max 50 chars
    "LastRestocked"     TEXT    NOT NULL     -- ISO 8601 datetime
);
```

## Known Limitations

### Race Condition in Order Creation
The monolith calls `CheckStockAsync` and `DeductStockAsync` as two separate HTTP requests. Between the check and the deduction, a concurrent request could exhaust stock. The original monolith code performed this atomically within a single database transaction.

**Potential solutions:**
- Replace with a single `deduct-if-available` endpoint that atomically checks and deducts
- Use optimistic concurrency with ETag/version column
- Accept the risk for low-contention scenarios

### No Resilience Policies
The monolith's `InventoryServiceHttpClient` has no retry, circuit breaker, or timeout-per-request policies. If the inventory service is down, all order creation and inventory views will hard-fail.

**Recommended additions:**
- Polly retry policy with exponential backoff
- Circuit breaker to fail fast when service is persistently down
- Timeout policy per request

### Denormalized Product Name
`ProductName` in the inventory item is set at creation time and not synced with the monolith's product catalog. If a product is renamed in the monolith, the inventory service will show the old name.

### No Schema Migrations
The database uses `EnsureCreated()` rather than EF Core migrations. This works for initial setup but does not support schema evolution. For production, switch to migration-based schema management.

## Security Considerations

- **CORS**: Currently configured to allow all origins (`AllowAnyOrigin`). Production deployments should restrict to known frontend origins.
- **No Authentication**: All endpoints are publicly accessible. Consider adding JWT bearer authentication or API key validation.
- **No Input Validation**: The controller accepts any integer for quantity. Consider adding `[Range]` attributes and model validation.
- **SQLite Concurrency**: SQLite supports limited concurrent writes. For high-traffic production use, consider PostgreSQL or another production-grade database.
