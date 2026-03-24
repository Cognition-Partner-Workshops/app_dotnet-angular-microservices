# Inventory Service

A microservice responsible for managing product stock levels, restocking operations, and low-stock alerts. Built with .NET 8 (ASP.NET Core) and an Angular 17 frontend.

## Overview

The Inventory Service tracks warehouse inventory for products. It provides:

- **Stock queries** — retrieve current quantities for all products or a specific product
- **Restocking** — add units to a product's on-hand quantity
- **Stock deduction** — remove units (e.g., when an order is fulfilled)
- **Low-stock alerts** — identify items at or below their reorder threshold

## Architecture

```
┌────────────────────────────────────────────────┐
│              Inventory Service                  │
│                                                 │
│  ┌──────────────┐     ┌─────────────────────┐  │
│  │ Angular 17   │────▶│  .NET 8 Web API     │  │
│  │ SPA (:4200)  │     │  (Kestrel :5000)    │  │
│  └──────────────┘     └──────────┬──────────┘  │
│                                  │              │
│                       ┌──────────▼──────────┐  │
│                       │  SQLite             │  │
│                       │  (inventory.db)     │  │
│                       └─────────────────────┘  │
└────────────────────────────────────────────────┘
```

### Backend Layers

| Layer | Location | Responsibility |
|-------|----------|---------------|
| Controllers | `src/.../Controllers/` | HTTP routing, request/response mapping |
| Services | `src/.../Services/` | Business logic (stock calculations, validation) |
| Data | `src/.../Data/` | EF Core DbContext, entity configuration, seed data |
| Models | `src/.../Models/` | Domain entities (`InventoryItem`) |

### Frontend

The Angular SPA uses standalone components with lazy-loaded routes:

| Route | Component | Description |
|-------|-----------|-------------|
| `/inventory` | `InventoryListComponent` | Displays a table of all inventory items; highlights low-stock rows |
| `/restock` | `InventoryRestockComponent` | Form to add stock to a product by its ID |

## Getting Started

### Prerequisites

- [.NET 8 SDK](https://dotnet.microsoft.com/download/dotnet/8.0)
- [Node.js 20](https://nodejs.org/) and npm

### Run the API

```bash
dotnet run --project src/InventoryService.Api
# API available at http://localhost:5000
# Swagger UI at http://localhost:5000/swagger
```

The database is automatically created and seeded with sample data on first run.

### Run the Angular Frontend

```bash
cd client-app
npm install
npm start
# App available at http://localhost:4200
# API calls are proxied to http://localhost:5000 via proxy.conf.json
```

### Run Tests

```bash
dotnet test --verbosity normal
```

Tests use EF Core's in-memory database provider for fast, isolated execution. See `tests/InventoryService.Api.Tests/InventoryServiceTests.cs` for coverage of all service methods including edge cases (insufficient stock, missing products, etc.).

## API Reference

Base URL: `http://localhost:5000`

### List All Inventory Items

```
GET /api/inventory
```

**Response** `200 OK`

```json
[
  {
    "id": 1,
    "productId": 1,
    "productName": "Widget A",
    "productSku": "WGT-001",
    "quantityOnHand": 50,
    "reorderLevel": 10,
    "warehouseLocation": "A-01",
    "lastRestocked": "2025-01-15T10:30:00Z"
  }
]
```

### Get Inventory by Product ID

```
GET /api/inventory/product/{productId}
```

**Response** `200 OK` — returns the inventory item, or `404 Not Found` if no record exists.

### Restock a Product

```
POST /api/inventory/product/{productId}/restock
Content-Type: application/json

{ "quantity": 25 }
```

**Response** `200 OK` — returns the updated inventory item with the new `quantityOnHand`.

**Error** `404 Not Found` — product does not exist.

### Deduct Stock

```
POST /api/inventory/product/{productId}/deduct
Content-Type: application/json

{ "quantity": 10 }
```

**Response** `200 OK` — returns the updated inventory item.

**Errors:**
- `404 Not Found` — product does not exist
- `400 Bad Request` — insufficient stock available

### List Low-Stock Items

```
GET /api/inventory/low-stock
```

Returns all items where `quantityOnHand <= reorderLevel`.

### Health Check

```
GET /health
```

Returns `200 OK` with `Healthy` status. Used by Kubernetes liveness and readiness probes.

## Data Model

### InventoryItem

| Field | Type | Description |
|-------|------|-------------|
| `Id` | `int` | Auto-generated primary key |
| `ProductId` | `int` | Unique product identifier |
| `ProductName` | `string` | Display name (max 200 chars) |
| `ProductSku` | `string` | Stock-keeping unit code (max 50 chars) |
| `QuantityOnHand` | `int` | Current stock level |
| `ReorderLevel` | `int` | Low-stock threshold (default: 10) |
| `WarehouseLocation` | `string` | Bin/aisle location (e.g., "A-01") |
| `LastRestocked` | `DateTime` | UTC timestamp of last restock |

## Seed Data

On first run, the database is populated with five sample products:

| ProductId | Name | SKU | Qty | Location |
|-----------|------|-----|-----|----------|
| 1 | Widget A | WGT-001 | 50 | A-01 |
| 2 | Widget B | WGT-002 | 100 | A-02 |
| 3 | Gadget X | GDG-001 | 150 | A-03 |
| 4 | Gadget Y | GDG-002 | 200 | A-04 |
| 5 | Thingamajig | THG-001 | 250 | A-05 |

## Docker

The multi-stage Dockerfile builds both the Angular SPA and the .NET API into a single container:

```bash
docker build -f docker/Dockerfile -t inventory-service:local .
docker run -p 8080:8080 inventory-service:local
# Full app (API + SPA) available at http://localhost:8080
```

**Build stages:**

1. **client-build** — Node 20 Alpine: installs npm dependencies, compiles Angular into static assets
2. **api-build** — .NET 8 SDK: restores NuGet packages, copies Angular output to `wwwroot`, publishes the API
3. **runtime** — ASP.NET 8 Alpine: minimal image serving both the API and the embedded SPA

## Kubernetes Deployment

### Helm Chart

The Helm chart at `helm/inventory-service/` includes:

| Template | Purpose |
|----------|---------|
| `deployment.yaml` | Pod spec with health probes, env vars, resource limits |
| `service.yaml` | ClusterIP service exposing port 80 → container port 8080 |
| `ingress.yaml` | Nginx Ingress with cert-manager TLS |
| `hpa.yaml` | Horizontal Pod Autoscaler (enabled in staging) |
| `networkpolicy.yaml` | Network isolation rules |
| `servicemonitor.yaml` | Prometheus metrics scraping |

### Environment-Specific Values

| File | Replicas | HPA | Persistence | Notes |
|------|----------|-----|-------------|-------|
| `values.yaml` | 1 | off | 1Gi gp2 | Base defaults |
| `values-dev.yaml` | 1 | off | disabled | Minimal resources, ephemeral DB |
| `values-staging.yaml` | 2 | 2–4 pods | inherited | Production-like with autoscaling |

### Deploy with Helm

```bash
# Dev
helm upgrade --install inventory-service helm/inventory-service \
  -f helm/inventory-service/values-dev.yaml -n dev --create-namespace

# Staging
helm upgrade --install inventory-service helm/inventory-service \
  -f helm/inventory-service/values-staging.yaml -n staging --create-namespace
```

### ArgoCD

Application manifests for dev and staging are provided in `argocd/`. These point to the Helm chart in this repository for GitOps-driven continuous deployment.

## CI/CD

The GitHub Actions workflow (`.github/workflows/build-push.yaml`) runs on pushes to `main` when files under `inventory-service/` change:

1. **Test** — runs `dotnet test` to gate on quality
2. **Auth** — authenticates to AWS via OIDC (no long-lived credentials)
3. **Build** — builds the multi-stage Docker image
4. **Push** — pushes to Amazon ECR with `:sha` and `:latest` tags

## Configuration

### appsettings.json

```json
{
  "ConnectionStrings": {
    "DefaultConnection": "Data Source=inventory.db"
  }
}
```

The connection string can be overridden via environment variables (e.g., `ConnectionStrings__DefaultConnection`) for container deployments where the SQLite file is stored on a persistent volume at `/data/inventory.db`.
