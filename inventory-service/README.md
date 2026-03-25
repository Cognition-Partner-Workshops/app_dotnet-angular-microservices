# Inventory Service

A standalone .NET 8 + Angular 17 microservice extracted from the OrderManager monolith. Manages stock levels, warehouse locations, and reorder alerts.

## Architecture

This microservice owns the **Inventory** bounded context with its own:
- SQLite database (no shared DB with monolith)
- REST API endpoints
- Angular frontend

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/inventory` | List all inventory items |
| GET | `/api/inventory/product/{productId}` | Get inventory for a product |
| POST | `/api/inventory/product/{productId}/restock` | Restock a product |
| GET | `/api/inventory/low-stock` | List low-stock items |
| GET | `/health` | Health check |

## Tech Stack

- **Backend**: .NET 8, C#, Entity Framework Core, SQLite
- **Frontend**: Angular 17, TypeScript
- **API**: RESTful with Swagger/OpenAPI

## Getting Started

### Prerequisites
- .NET 8 SDK
- Node.js 18+
- Angular CLI (`npm install -g @angular/cli`)

### Run locally

```bash
# Restore .NET dependencies
dotnet restore src/InventoryService.Api/InventoryService.Api.csproj

# Install Angular dependencies
cd client-app && npm install && cd ..

# Run tests
dotnet test

# Run the API (serves Angular app too)
dotnet run --project src/InventoryService.Api/InventoryService.Api.csproj
```

The service will be available at `https://localhost:5001`.

## Deployment

- **Docker**: `docker build -f docker/Dockerfile -t inventory-service .`
- **Helm**: `helm install inventory ./helm/inventory-service`
- **ArgoCD**: Apply manifests in `argocd/`

## License

MIT
