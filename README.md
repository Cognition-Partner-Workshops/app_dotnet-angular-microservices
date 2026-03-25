# Microservices — OrderManager Decomposition

Microservices extracted from the [OrderManager monolith](https://github.com/Cognition-Partner-Workshops/app_dotnet-angular-monolith). Each service is independently deployable with its own database, Helm chart, Dockerfile, and CI/CD pipeline.

## Services

| Service | Path | Description |
|---------|------|-------------|
| **inventory-service** | `services/inventory-service/` | Stock levels, warehouse locations, reorder alerts, restock/deduct operations |

## Inventory Service

### Tech Stack

- **Backend**: .NET 8, C#, Entity Framework Core, SQLite
- **Frontend**: Angular 17, TypeScript
- **API**: RESTful with Swagger/OpenAPI
- **Container**: Multi-stage Docker build (Node + .NET SDK + aspnet runtime)
- **Orchestration**: Helm chart, ArgoCD, HPA, NetworkPolicy, ServiceMonitor

### API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/inventory` | List all inventory items |
| GET | `/api/inventory/product/{productId}` | Get inventory for a product |
| POST | `/api/inventory/product/{productId}/restock` | Restock a product |
| POST | `/api/inventory/product/{productId}/deduct` | Deduct stock (used by OrderManager) |
| GET | `/api/inventory/low-stock` | List items at or below reorder level |
| GET | `/health` | Health check |

### Getting Started

```bash
cd services/inventory-service

# Restore .NET dependencies
dotnet restore

# Install Angular dependencies
cd client-app && npm install && cd ../..

# Run the API (serves Angular app too)
dotnet run --project src/InventoryService.Api/InventoryService.Api.csproj
```

The application will be available at `http://localhost:5002`.

### IaC

- **Dockerfile**: `services/inventory-service/docker/Dockerfile` — multi-stage build
- **Helm chart**: `services/inventory-service/helm/inventory-service/` — deployment, service, ingress, network policy, service monitor, HPA
- **ArgoCD**: `services/inventory-service/argocd/` — application manifests for dev and staging
- **CI/CD**: `.github/workflows/inventory-service-ci.yaml` — build, test, push to ECR, trigger ArgoCD sync

### Monolith Integration

The OrderManager monolith calls this service via HTTP (`InventoryServiceClient`) to check and deduct inventory during order creation, replacing the previous in-process `InventoryService` dependency.

## Platform Conformance

All services conform to the [platform-engineering-shared-services](https://github.com/Cognition-Partner-Workshops/platform-engineering-shared-services) standard and follow the IaC patterns from [app_dotnet-angular-monolith-iac](https://github.com/Cognition-Partner-Workshops/app_dotnet-angular-monolith-iac).

## License

MIT
