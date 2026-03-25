# Microservices — Decomposed from OrderManager Monolith

Standalone microservices extracted from the [OrderManager monolith](https://github.com/Cognition-Partner-Workshops/app_dotnet-angular-monolith). Each service owns its own data, API, frontend, Dockerfile, Helm chart, and CI/CD pipeline, conforming to the [platform-engineering-shared-services](https://github.com/Cognition-Partner-Workshops/platform-engineering-shared-services) standard.

## Services

| Service | Description | Port |
|---------|-------------|------|
| **inventory-service** | Stock levels, warehouse locations, reorder alerts, reserve/release for order fulfillment | 8080 |

## Inventory Service

### Tech Stack
- **Backend**: .NET 8, C#, Entity Framework Core, SQLite
- **Frontend**: Angular 17, TypeScript
- **API**: RESTful with Swagger/OpenAPI

### API Endpoints
| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/inventory` | List all inventory items |
| GET | `/api/inventory/product/{id}` | Get inventory for a specific product |
| POST | `/api/inventory/product/{id}/restock` | Restock a product |
| GET | `/api/inventory/low-stock` | List items at or below reorder level |
| POST | `/api/inventory/product/{id}/reserve` | Reserve stock (called by order service) |
| POST | `/api/inventory/product/{id}/release` | Release reserved stock |
| GET | `/health` | Health check |

### Running Locally

```bash
# Restore and run the API
cd inventory-service
dotnet restore
dotnet run --project src/InventoryService.Api/InventoryService.Api.csproj

# Run tests
dotnet test

# Install Angular dependencies and build the frontend
cd client-app && npm install && npm run build
```

The service will be available at `http://localhost:5000`.

## IaC

| Component | Path | Description |
|-----------|------|-------------|
| Dockerfile | `docker/Dockerfile` | Multi-stage build (Angular → .NET → Alpine runtime) |
| Helm chart | `helm/inventory-service/` | Deployment, Service, NetworkPolicy, Ingress, HPA, ServiceMonitor |
| ArgoCD | `argocd/` | Application manifests for dev and staging environments |
| CI/CD | `.github/workflows/build-push.yaml` | Build, test, push to ECR |

## License

MIT
