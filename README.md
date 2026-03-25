# Microservices — OrderManager Decomposition

This repository contains microservices decomposed from the [OrderManager monolith](https://github.com/Cognition-Partner-Workshops/app_dotnet-angular-monolith).

## Services

### inventory-service

Standalone .NET 8 Web API managing stock levels, warehouse locations, and reorder thresholds.

| Component | Tech |
|-----------|------|
| Backend | .NET 8, C#, EF Core, SQLite |
| Frontend | Angular 17, TypeScript |
| Container | Multi-stage Docker build |
| Orchestration | Helm chart, ArgoCD, HPA |
| CI/CD | GitHub Actions → ECR → ArgoCD |
| Monitoring | Prometheus ServiceMonitor |

#### Quick Start

```bash
cd services/inventory-service

# Restore and run the API
dotnet restore src/InventoryService.Api/InventoryService.Api.csproj
dotnet run --project src/InventoryService.Api/InventoryService.Api.csproj

# Run tests
dotnet test --verbosity normal

# Install Angular dependencies and build client
cd client-app && npm install && npm run build && cd ..
```

The API will be available at `http://localhost:5000` with Swagger at `/swagger`.

#### API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/inventory` | List all inventory items |
| GET | `/api/inventory/product/{id}` | Get inventory by product ID |
| POST | `/api/inventory/product/{id}/restock` | Restock a product |
| GET | `/api/inventory/low-stock` | List low-stock items |
| GET | `/api/inventory/product/{id}/check-stock` | Check stock availability |
| POST | `/api/inventory/product/{id}/deduct` | Deduct stock (used by order service) |
| GET | `/health` | Health check |

#### IaC

- `docker/Dockerfile` — Multi-stage build
- `helm/inventory-service/` — Helm chart with deployment, service, network policy, HPA, service monitor
- `argocd/` — ArgoCD application manifests for dev and staging
- `ci/build-push.yaml` — GitHub Actions CI/CD pipeline

## Platform Conformance

All services conform to the [platform-engineering-shared-services](https://github.com/Cognition-Partner-Workshops/platform-engineering-shared-services) standard including namespace isolation, network policies, Prometheus monitoring, and ArgoCD GitOps deployments.
