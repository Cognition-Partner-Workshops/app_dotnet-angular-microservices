# Microservices — Decomposed from OrderManager Monolith

This repository contains microservices extracted from the [OrderManager monolith](https://github.com/Cognition-Partner-Workshops/app_dotnet-angular-monolith).

## Services

### Inventory Service

A standalone .NET 8 Web API managing stock levels, warehouse locations, and reorder operations.

**Tech Stack**
- Backend: .NET 8, C#, Entity Framework Core, SQLite
- Frontend: Angular 17, TypeScript
- API: RESTful with Swagger/OpenAPI

**API Endpoints**
| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/inventory` | List all inventory items |
| GET | `/api/inventory/product/{productId}` | Get inventory by product ID |
| POST | `/api/inventory/product/{productId}/restock` | Restock a product |
| POST | `/api/inventory/product/{productId}/deduct` | Deduct stock (used by order service) |
| GET | `/api/inventory/low-stock` | List items at or below reorder level |
| GET | `/health` | Health check |

**Getting Started**
```bash
# Restore and run
cd inventory-service
dotnet restore
dotnet run --project src/InventoryService.Api/InventoryService.Api.csproj

# Run tests
dotnet test
```

## Infrastructure

- `docker/` — Multi-stage Dockerfile
- `helm/inventory-service/` — Helm chart (deployment, service, network policy, service monitor, HPA)
- `argocd/` — ArgoCD application manifests for dev and staging
- `.github/workflows/` — CI/CD pipeline (build, test, push to ECR)

Conforms to [platform-engineering-shared-services](https://github.com/Cognition-Partner-Workshops/platform-engineering-shared-services) standards.
