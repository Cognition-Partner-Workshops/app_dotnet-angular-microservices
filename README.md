# Inventory Microservice

A standalone .NET 8 + Angular 17 microservice decomposed from the [OrderManager monolith](https://github.com/Cognition-Partner-Workshops/app_dotnet-angular-monolith). Manages stock levels, warehouse locations, and reorder alerts independently.

## Architecture

| Component | Description |
|-----------|-------------|
| **API** | .NET 8 Web API with EF Core + SQLite |
| **Frontend** | Angular 17 standalone components |
| **Docker** | Multi-stage build (Node → .NET SDK → ASP.NET runtime) |
| **Helm** | Kubernetes deployment, service, network policy, HPA, service monitor |
| **ArgoCD** | GitOps application manifests for dev and staging |
| **CI/CD** | GitHub Actions — build, test, push to ECR |

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/inventory` | List all inventory items |
| GET | `/api/inventory/product/{id}` | Get inventory by product ID |
| POST | `/api/inventory/product/{id}/restock` | Restock a product |
| GET | `/api/inventory/low-stock` | List items at or below reorder level |
| GET | `/api/inventory/product/{id}/check?quantity=N` | Check stock availability |
| POST | `/api/inventory/product/{id}/deduct` | Deduct stock (used by order service) |
| GET | `/health` | Health check endpoint |

## Getting Started

### Prerequisites
- .NET 8 SDK
- Node.js 18+
- Angular CLI (`npm install -g @angular/cli`)

### Run locally

```bash
# Restore and run API
dotnet restore src/InventoryService.Api/InventoryService.Api.csproj
dotnet run --project src/InventoryService.Api/InventoryService.Api.csproj

# Install and build Angular client
cd client-app && npm install && npm run build && cd ..

# Run tests
dotnet test
```

The API will be available at `https://localhost:5001`.

## Platform Conformance

This service conforms to the [platform-engineering-shared-services](https://github.com/Cognition-Partner-Workshops/platform-engineering-shared-services) standard:
- Namespace isolation with network policies
- Prometheus ServiceMonitor for observability
- ArgoCD-driven GitOps deployments
- ECR container registry with lifecycle policies

## License

MIT
