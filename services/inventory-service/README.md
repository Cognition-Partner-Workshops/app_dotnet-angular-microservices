# Inventory Service

A standalone .NET 8 Web API microservice decomposed from the [OrderManager monolith](https://github.com/Cognition-Partner-Workshops/app_dotnet-angular-monolith). Manages stock levels, warehouse locations, restocking, and stock availability checks.

## Architecture

This microservice owns the **Inventory** bounded context, previously a tightly-coupled module inside the OrderManager monolith. It has its own database, API surface, and Angular frontend.

| Component | Technology |
|-----------|-----------|
| **Backend** | .NET 8, C#, Entity Framework Core, SQLite |
| **Frontend** | Angular 17, TypeScript |
| **API** | RESTful with Swagger/OpenAPI |
| **Container** | Multi-stage Docker build (Alpine) |
| **Orchestration** | Kubernetes (Helm chart + ArgoCD) |
| **CI/CD** | GitHub Actions → ECR → ArgoCD |

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/inventory` | List all inventory items |
| GET | `/api/inventory/product/{productId}` | Get inventory for a specific product |
| POST | `/api/inventory/product/{productId}/restock` | Restock a product |
| POST | `/api/inventory/product/{productId}/deduct` | Deduct stock (used by Order service) |
| GET | `/api/inventory/low-stock` | List items at or below reorder level |
| GET | `/health` | Health check endpoint |

## Tech Stack

- **Backend**: .NET 8, C#, Entity Framework Core, SQLite
- **Frontend**: Angular 17, TypeScript
- **API**: RESTful with Swagger/OpenAPI

## Getting Started

### Prerequisites
- .NET 8 SDK
- Node.js 18+
- Angular CLI (`npm install -g @angular/cli`)

### Run Locally

```bash
# Restore .NET dependencies
dotnet restore src/InventoryService.Api/InventoryService.Api.csproj

# Install Angular dependencies
cd client-app && npm install && cd ..

# Run the API (serves Angular app too)
dotnet run --project src/InventoryService.Api/InventoryService.Api.csproj

The service will be available at `http://localhost:5000`.

### Run Tests

```bash
dotnet test --verbosity normal
```

## Infrastructure

- **Dockerfile**: `docker/Dockerfile` — multi-stage build (Angular + .NET + runtime)
- **Helm chart**: `helm/inventory-service/` — Kubernetes deployment manifests
- **ArgoCD**: `argocd/` — GitOps application manifests for dev and staging
- **CI/CD**: `ci/build-push.yaml` — GitHub Actions pipeline
