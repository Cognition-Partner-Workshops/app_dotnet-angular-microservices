# Inventory Service

A standalone .NET 8 Web API + Angular 17 microservice extracted from the [OrderManager monolith](https://github.com/Cognition-Partner-Workshops/app_dotnet-angular-monolith). This service owns the entire inventory domain: stock levels, warehouse locations, restocking, low-stock alerts, and stock availability checks.

## Table of Contents

- [Architecture Overview](#architecture-overview)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [API Reference](#api-reference)
- [Data Model](#data-model)
- [Frontend Components](#frontend-components)
- [Testing](#testing)
- [Docker](#docker)
- [Kubernetes Deployment](#kubernetes-deployment)
- [CI/CD Pipeline](#cicd-pipeline)
- [Configuration](#configuration)
- [Integration with OrderManager Monolith](#integration-with-ordermanager-monolith)
- [Monitoring and Health Checks](#monitoring-and-health-checks)

## Architecture Overview

```
┌─────────────────────────────────┐       ┌──────────────────────────────────┐
│      OrderManager Monolith      │       │     Inventory Service (this)     │
│                                 │       │                                  │
│  ┌───────────┐  ┌────────────┐  │  HTTP │  ┌────────────┐  ┌───────────┐  │
│  │ OrderSvc  │──│ Inventory  │──│──────>│  │ Controller │──│ Business  │  │
│  │           │  │ HttpClient │  │       │  │            │  │ Service   │  │
│  └───────────┘  └────────────┘  │       │  └────────────┘  └─────┬─────┘  │
│                                 │       │                        │        │
│  ┌───────────┐                  │       │                  ┌─────┴─────┐  │
│  │ Inventory │  (BFF proxy)     │       │                  │  SQLite   │  │
│  │ Controller│──────────────────│──────>│                  │ inventory │  │
│  └───────────┘                  │       │                  │   .db     │  │
└─────────────────────────────────┘       └──────────────────┴───────────┘  │
                                                                            │
┌─────────────────────────────────┐                                         │
│    Angular 17 Frontend          │─────────────────────────────────────────┘
│  (inventory-list, low-stock)    │  HTTP (direct or via monolith proxy)
└─────────────────────────────────┘
```

The inventory-service owns its own SQLite database and exposes REST endpoints consumed by:
- **OrderManager monolith** via `InventoryServiceHttpClient` for stock checks and deductions during order creation
- **Monolith's InventoryController** acts as a BFF proxy, forwarding `/api/inventory` requests to this service
- **Angular frontend** for inventory management UI (list, restock, low-stock alerts)

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Runtime | .NET 8 (ASP.NET Core) |
| Language | C# 12 |
| ORM | Entity Framework Core 8.0 (SQLite provider) |
| API Docs | Swagger / OpenAPI (Swashbuckle) |
| Frontend | Angular 17 (standalone components) |
| Container | Multi-stage Docker build (Node 20 + .NET 8 SDK + ASP.NET 8 Alpine runtime) |
| Orchestration | Kubernetes with Helm 3 |
| GitOps | ArgoCD |
| CI/CD | GitHub Actions |
| Registry | Amazon ECR |
| Monitoring | Prometheus ServiceMonitor |

## Project Structure

```
services/inventory-service/
├── InventoryService.sln              # Solution file
├── README.md                         # This file
├── src/
│   └── InventoryService.Api/
│       ├── InventoryService.Api.csproj
│       ├── Program.cs                # Application entry point and DI configuration
│       ├── appsettings.json          # Application configuration
│       ├── Controllers/
│       │   └── InventoryController.cs # REST API endpoints
│       ├── Data/
│       │   ├── InventoryDbContext.cs  # EF Core DbContext
│       │   └── SeedData.cs           # Initial seed data for development
│       ├── Models/
│       │   └── InventoryItem.cs      # Domain model
│       └── Services/
│           └── InventoryService.cs   # Business logic layer
├── tests/
│   └── InventoryService.Api.Tests/
│       ├── InventoryService.Api.Tests.csproj
│       └── InventoryServiceTests.cs  # xUnit integration tests
├── client-app/                       # Angular 17 frontend
│   └── src/app/modules/inventory/
│       ├── inventory-list.component.ts
│       └── low-stock.component.ts
├── docker/
│   └── Dockerfile                    # Multi-stage Docker build
├── helm/
│   └── inventory-service/
│       ├── Chart.yaml
│       ├── values.yaml               # Base Helm values
│       ├── values-dev.yaml           # Dev environment overrides
│       ├── values-staging.yaml       # Staging environment overrides
│       └── templates/
│           ├── _helpers.tpl
│           ├── deployment.yaml
│           ├── service.yaml
│           ├── ingress.yaml
│           ├── hpa.yaml
│           ├── networkpolicy.yaml
│           └── servicemonitor.yaml
├── argocd/
│   ├── application-dev.yaml          # ArgoCD Application for dev
│   └── application-staging.yaml      # ArgoCD Application for staging
└── ci/
    └── build-push.yaml               # GitHub Actions CI/CD pipeline definition
```

## Getting Started

### Prerequisites

- [.NET 8 SDK](https://dotnet.microsoft.com/download/dotnet/8.0)
- [Node.js 18+](https://nodejs.org/) (for Angular frontend)
- [Angular CLI](https://angular.io/cli) (`npm install -g @angular/cli`)
- [Docker](https://docs.docker.com/get-docker/) (optional, for container builds)
- [Helm 3](https://helm.sh/docs/intro/install/) (optional, for Kubernetes deployment)

### Run the API locally

```bash
cd services/inventory-service

# Restore .NET dependencies
dotnet restore

# Run the API
dotnet run --project src/InventoryService.Api/InventoryService.Api.csproj --urls "http://localhost:5100"
```

The API will be available at `http://localhost:5100` with Swagger UI at `http://localhost:5100/swagger`.

On first startup, the database is automatically created and seeded with 5 sample inventory items.

### Run the Angular frontend

```bash
cd services/inventory-service/client-app

# Install dependencies
npm install

# Start the dev server
ng serve
```

The frontend will be available at `http://localhost:4200`.

### Run with the OrderManager monolith

To test the full integration:

```bash
# Terminal 1: Start inventory-service
cd services/inventory-service
dotnet run --project src/InventoryService.Api/InventoryService.Api.csproj --urls "http://localhost:5100"

# Terminal 2: Start OrderManager monolith (from its repo)
cd /path/to/app_dotnet-angular-monolith
dotnet run --project src/OrderManager.Api/OrderManager.Api.csproj --urls "http://localhost:5000"
```

The monolith reads `InventoryService:BaseUrl` from its `appsettings.json` (defaults to `http://localhost:5100`).

## API Reference

Base URL: `http://localhost:5100`

### GET /api/inventory

List all inventory items.

**Response:** `200 OK`
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
  }
]
```

### GET /api/inventory/product/{productId}

Get inventory for a specific product.

**Parameters:**
| Name | In | Type | Description |
|------|----|------|-------------|
| `productId` | path | int | The product ID |

**Response:** `200 OK` with inventory item, or `404 Not Found` if no record exists.

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

### POST /api/inventory/product/{productId}/restock

Restock a product by adding quantity.

**Parameters:**
| Name | In | Type | Description |
|------|----|------|-------------|
| `productId` | path | int | The product ID |

**Request Body:**
```json
{
  "quantity": 25
}
```

**Response:** `200 OK` with updated inventory item. `LastRestocked` is updated to current UTC time.

### GET /api/inventory/product/{productId}/check

Check if sufficient stock is available for a given quantity.

**Parameters:**
| Name | In | Type | Description |
|------|----|------|-------------|
| `productId` | path | int | The product ID |
| `quantity` | query | int | Quantity to check (default: 1) |

**Response:** `200 OK`
```json
{
  "productId": 1,
  "quantity": 5,
  "available": true
}
```

### POST /api/inventory/product/{productId}/deduct

Deduct stock from a product. Used by the order service during order creation.

**Parameters:**
| Name | In | Type | Description |
|------|----|------|-------------|
| `productId` | path | int | The product ID |

**Request Body:**
```json
{
  "quantity": 2
}
```

**Response:**
- `200 OK` with updated inventory item on success
- `409 Conflict` if insufficient stock: `{ "error": "Insufficient stock for product {id}. Available: {qty}" }`

### GET /api/inventory/low-stock

List all items where `quantityOnHand <= reorderLevel`.

**Response:** `200 OK` with array of inventory items at or below their reorder threshold.

### GET /health

Health check endpoint.

**Response:** `200 OK` with `Healthy` text.

## Data Model

### InventoryItem

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| `Id` | int | PK, auto-increment | Unique identifier |
| `ProductId` | int | required | Foreign key to the monolith's Product table |
| `ProductName` | string | required, max 200 chars | Denormalized product name for display |
| `QuantityOnHand` | int | required | Current stock level |
| `ReorderLevel` | int | default: 10 | Threshold for low-stock alerts |
| `WarehouseLocation` | string | max 50 chars | Physical warehouse location code |
| `LastRestocked` | DateTime | default: UTC now | Timestamp of last restock operation |

### Seed Data

The service auto-seeds 5 inventory items on first startup (when the database is empty):

| ProductId | ProductName | QuantityOnHand | ReorderLevel | Location |
|-----------|-------------|----------------|--------------|----------|
| 1 | Widget A | 50 | 10 | A-01 |
| 2 | Widget B | 100 | 10 | A-02 |
| 3 | Gadget X | 150 | 10 | A-03 |
| 4 | Gadget Y | 200 | 10 | A-04 |
| 5 | Thingamajig | 250 | 10 | A-05 |

## Frontend Components

The Angular 17 frontend provides two standalone components:

### InventoryListComponent (`inventory-list.component.ts`)

Displays all inventory items in a table with:
- Product name, quantity on hand, reorder level, warehouse location, last restocked date
- Rows highlighted when stock is at or below the reorder level
- Inline restock form (quantity input + restock button per row)
- Auto-refreshes after restock operations

### LowStockComponent (`low-stock.component.ts`)

Displays only items where `quantityOnHand <= reorderLevel`:
- Shows a table of low-stock items
- Displays "All items are adequately stocked" when no items are below threshold

Both components use `HttpClient` to call the inventory API and are configured via the `environment.apiUrl` setting.

## Testing

### Run tests

```bash
cd services/inventory-service
dotnet test
```

### Test coverage

The test suite (`InventoryServiceTests.cs`) contains 6 xUnit tests covering:

| Test | Description |
|------|-------------|
| `GetAllInventory_ReturnsSeedData` | Verifies all 5 seed items are returned |
| `GetByProductId_ReturnsCorrectItem` | Verifies lookup by product ID returns the correct item |
| `Restock_IncreasesQuantity` | Verifies restocking adds to `QuantityOnHand` |
| `DeductStock_DecreasesQuantity` | Verifies deduction subtracts from `QuantityOnHand` |
| `DeductStock_ThrowsOnInsufficientStock` | Verifies `InvalidOperationException` when deducting more than available |
| `GetLowStockItems_ReturnsEmptyWhenAllStocked` | Verifies empty result when all items are above reorder level |

Tests use EF Core's `InMemoryDatabase` provider with a unique database name per test for isolation.

## Docker

### Build the container image

```bash
cd services/inventory-service
docker build -f docker/Dockerfile -t inventory-service:latest .
```

### Multi-stage build process

| Stage | Base Image | Purpose |
|-------|-----------|---------|
| 1 (`client-build`) | `node:20-alpine` | Build Angular frontend (`npm ci` + `npm run build`) |
| 2 (`api-build`) | `mcr.microsoft.com/dotnet/sdk:8.0` | Restore, build, and publish .NET API; copy Angular dist into `wwwroot/` |
| 3 (`runtime`) | `mcr.microsoft.com/dotnet/aspnet:8.0-alpine` | Minimal runtime image with published output |

The final image exposes port **8080** and runs as `dotnet InventoryService.Api.dll`.

### Run the container

```bash
docker run -p 5100:8080 inventory-service:latest
```

## Kubernetes Deployment

### Helm chart

The Helm chart is located at `helm/inventory-service/` and includes:

| Template | Description |
|----------|-------------|
| `deployment.yaml` | Kubernetes Deployment with health probes and resource limits |
| `service.yaml` | ClusterIP Service exposing port 80 -> 8080 |
| `ingress.yaml` | NGINX Ingress with cert-manager TLS |
| `hpa.yaml` | Horizontal Pod Autoscaler (configurable) |
| `networkpolicy.yaml` | Default-deny with explicit allow rules |
| `servicemonitor.yaml` | Prometheus ServiceMonitor for metrics scraping |

### Environment configurations

| Environment | Replicas | CPU Request | Memory Request | Autoscaling | Ingress Host |
|------------|----------|-------------|----------------|-------------|--------------|
| **Dev** | 1 | 50m | 128Mi | Disabled | `inventory-dev.workshop.local` |
| **Staging** | 2 | 100m | 256Mi | Enabled (2-4 pods, 75% CPU) | `inventory-staging.workshop.local` |
| **Production** | 1 (base) | 100m | 256Mi | Disabled (base) | `inventory.workshop.local` |

### Deploy with Helm

```bash
# Dev environment
helm upgrade --install inventory-service helm/inventory-service \
  -f helm/inventory-service/values.yaml \
  -f helm/inventory-service/values-dev.yaml \
  -n inventory-dev

# Staging environment
helm upgrade --install inventory-service helm/inventory-service \
  -f helm/inventory-service/values.yaml \
  -f helm/inventory-service/values-staging.yaml \
  -n inventory-staging
```

### Lint the Helm chart

```bash
helm lint helm/inventory-service
```

### ArgoCD

ArgoCD Application manifests are in `argocd/`:
- `application-dev.yaml` targets the `inventory-dev` namespace with `values-dev.yaml` overrides
- `application-staging.yaml` targets the `inventory-staging` namespace with `values-staging.yaml` overrides

Both use automated sync with self-healing and pruning enabled.

## CI/CD Pipeline

The GitHub Actions pipeline (`ci/build-push.yaml`) runs on:
- **Push** to `main` when files under `services/inventory-service/` change
- **Pull requests** to `main` when files under `services/inventory-service/` change

### Pipeline jobs

| Job | Trigger | Steps |
|-----|---------|-------|
| `build-and-test` | Push + PR | Checkout, setup .NET 8, restore, build (Release), run tests |
| `build-and-push` | Push to `main` only | Checkout, configure AWS (OIDC), login to ECR, build + push Docker image |

### Container registry

Images are pushed to: `599083837640.dkr.ecr.us-east-1.amazonaws.com/workshop/inventory-service`

Tags:
- `{git-sha}` for traceability
- `latest` for convenience

## Configuration

### Application settings (`appsettings.json`)

```json
{
  "ConnectionStrings": {
    "DefaultConnection": "Data Source=inventory.db"
  },
  "Logging": {
    "LogLevel": {
      "Default": "Information",
      "Microsoft.AspNetCore": "Warning"
    }
  }
}
```

### Environment variables

| Variable | Description | Default |
|----------|-------------|---------|
| `ConnectionStrings__DefaultConnection` | SQLite connection string | `Data Source=inventory.db` |
| `ASPNETCORE_ENVIRONMENT` | Runtime environment (`Development`, `Staging`, `Production`) | `Production` |
| `ASPNETCORE_URLS` | Listening URLs | `http://+:8080` (in Docker) |

### Overriding configuration

Configuration can be overridden via:
1. `appsettings.{Environment}.json` files
2. Environment variables (using `__` as section separator)
3. Command-line arguments (`--urls`, etc.)

## Integration with OrderManager Monolith

The monolith communicates with this service via HTTP using the `IInventoryServiceClient` interface and `InventoryServiceHttpClient` implementation.

### Monolith configuration

In the monolith's `appsettings.json`:
```json
{
  "InventoryService": {
    "BaseUrl": "http://localhost:5100"
  }
}
```

### Integration points

| Monolith Operation | HTTP Call to Inventory Service |
|--------------------|-------------------------------|
| List inventory (BFF proxy) | `GET /api/inventory` |
| Get inventory by product (BFF proxy) | `GET /api/inventory/product/{id}` |
| Restock product (BFF proxy) | `POST /api/inventory/product/{id}/restock` |
| Low-stock alerts (BFF proxy) | `GET /api/inventory/low-stock` |
| Order creation: check stock | `GET /api/inventory/product/{id}/check?quantity=N` |
| Order creation: deduct stock | `POST /api/inventory/product/{id}/deduct` |

### Flow: Order creation

```
1. User submits order via monolith API
2. OrderService iterates over order items:
   a. Calls CheckStockAsync(productId, quantity) -> GET /check
   b. If available, calls DeductStockAsync(productId, quantity) -> POST /deduct
3. If all stock checks and deductions succeed, order is saved to monolith DB
4. If any check fails, throws InvalidOperationException("Insufficient stock")
```

## Monitoring and Health Checks

### Health endpoint

`GET /health` returns `Healthy` with HTTP 200 when the application is running.

### Prometheus metrics

The Helm chart includes a `ServiceMonitor` resource that configures Prometheus to scrape metrics from the `/metrics` endpoint on port 8080.

### Kubernetes probes

The Deployment template configures liveness and readiness probes against the `/health` endpoint.

## License

MIT
