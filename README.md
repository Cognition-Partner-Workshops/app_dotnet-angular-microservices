# Inventory Microservice

A .NET 8 + Angular 17 microservice extracted from the [OrderManager monolith](https://github.com/Cognition-Partner-Workshops/app_dotnet-angular-monolith). Manages stock levels, warehouse locations, and reorder alerts independently.

## Architecture

This microservice owns the **Inventory** bounded context:

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/inventory` | GET | List all inventory items |
| `/api/inventory/product/{id}` | GET | Get inventory for a specific product |
| `/api/inventory/product/{id}/restock` | POST | Restock a product |
| `/api/inventory/product/{id}/check` | GET | Check stock availability |
| `/api/inventory/product/{id}/deduct` | POST | Deduct stock (called by monolith) |
| `/api/inventory/low-stock` | GET | List items at or below reorder level |
| `/health` | GET | Health check |

## Tech Stack

- **Backend**: .NET 8, C#, Entity Framework Core, SQLite
- **Frontend**: Angular 17, TypeScript
- **API**: RESTful with Swagger/OpenAPI
- **Container**: Multi-stage Docker build
- **Orchestration**: Helm chart for Kubernetes
- **GitOps**: ArgoCD application manifests
- **CI/CD**: GitHub Actions

## Getting Started

### Prerequisites
- .NET 8 SDK
- Node.js 18+
- Angular CLI (`npm install -g @angular/cli`)

### Run the application

```bash
# Restore and run the API
dotnet restore src/InventoryService.Api/InventoryService.Api.csproj
dotnet run --project src/InventoryService.Api/InventoryService.Api.csproj
```

The API will be available at `http://localhost:5000`.

### Run tests

```bash
dotnet test
```

## IaC

- `docker/Dockerfile` — Multi-stage build
- `helm/inventory-service/` — Helm chart (deployment, service, network policy, service monitor, HPA)
- `argocd/` — ArgoCD application manifests (dev, staging)
- `ci/build-push.yaml` — GitHub Actions CI/CD pipeline

## Platform Conformance

Conforms to [platform-engineering-shared-services](https://github.com/Cognition-Partner-Workshops/platform-engineering-shared-services) standards for namespaces, network policies, monitoring, and ArgoCD.

## License

MIT
