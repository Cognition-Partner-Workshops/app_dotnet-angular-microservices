# OrderManager Microservices

Microservices decomposed from the [OrderManager monolith](https://github.com/Cognition-Partner-Workshops/app_dotnet-angular-monolith). Each service is independently deployable and conforms to the [platform-engineering-shared-services](https://github.com/Cognition-Partner-Workshops/platform-engineering-shared-services) standard.

## Services

| Service | Description | Port |
|---------|-------------|------|
| **inventory-service** | Stock levels, warehouse locations, reorder alerts | 8080 |

## Repository Structure

```
services/
  inventory-service/
    src/InventoryService.Api/       # .NET 8 Web API
    tests/InventoryService.Api.Tests/  # Unit tests
    client-app/                     # Angular 17 frontend
    InventoryService.sln            # Solution file
infrastructure/
  inventory-service/
    docker/Dockerfile               # Multi-stage build
    helm/inventory-service/         # Helm chart
    argocd/                         # ArgoCD application manifests
.github/workflows/
  inventory-service-ci.yaml         # CI/CD pipeline
```

## Getting Started

### Prerequisites
- .NET 8 SDK
- Node.js 18+
- Angular CLI (`npm install -g @angular/cli`)

### Run the Inventory Service

```bash
# Restore and run the API
cd services/inventory-service
dotnet restore src/InventoryService.Api/InventoryService.Api.csproj
dotnet run --project src/InventoryService.Api/InventoryService.Api.csproj

# Run tests
dotnet test
```

The API will be available at `http://localhost:5002`.

### API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/inventory` | List all inventory items |
| GET | `/api/inventory/product/{id}` | Get inventory by product ID |
| POST | `/api/inventory/product/{id}/restock` | Restock a product |
| GET | `/api/inventory/low-stock` | Get low stock alerts |
| GET | `/api/inventory/product/{id}/check?quantity=N` | Check stock availability |
| POST | `/api/inventory/product/{id}/deduct` | Deduct stock |
| GET | `/health` | Health check endpoint |

## Platform Conformance

Each service includes:
- Helm chart with deployment, service, network policy, service monitor, HPA
- ArgoCD application manifests for dev and staging environments
- Multi-stage Dockerfile following platform patterns
- GitHub Actions CI/CD pipeline (build, test, push to ECR)
- Health check endpoint at `/health`
- Prometheus metrics support via ServiceMonitor

## Related Repos

- [app_dotnet-angular-monolith](https://github.com/Cognition-Partner-Workshops/app_dotnet-angular-monolith) — Original monolith
- [app_dotnet-angular-monolith-iac](https://github.com/Cognition-Partner-Workshops/app_dotnet-angular-monolith-iac) — Monolith IaC (pattern reference)
- [platform-engineering-shared-services](https://github.com/Cognition-Partner-Workshops/platform-engineering-shared-services) — Platform standard
