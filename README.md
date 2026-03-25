# OrderManager Microservices

Decomposed microservices from the [OrderManager monolith](https://github.com/Cognition-Partner-Workshops/app_dotnet-angular-monolith). Each service is independently deployable and conforms to the [platform-engineering-shared-services](https://github.com/Cognition-Partner-Workshops/platform-engineering-shared-services) standard.

## Architecture

### Inventory Service

Manages stock levels, warehouse locations, and reorder thresholds. Provides a stock reservation API consumed by the monolith's Order module during decomposition.

**Tech Stack:** .NET 8, EF Core (SQLite), Angular 17

**API Endpoints:**

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/inventory` | List all inventory items |
| GET | `/api/inventory/product/{id}` | Get inventory for a specific product |
| POST | `/api/inventory/product/{id}/restock` | Restock a product |
| POST | `/api/inventory/product/{id}/deduct` | Deduct stock (order fulfillment) |
| GET | `/api/inventory/low-stock` | List items at or below reorder level |
| POST | `/api/inventory/check-and-reserve` | Atomically check and reserve stock |
| GET | `/health` | Health check |

### OpenAPI / Swagger

Each service exposes an interactive OpenAPI specification:

| Resource | URL |
|----------|-----|
| Swagger UI | `/swagger/index.html` |
| OpenAPI JSON | `/swagger/v1/swagger.json` |

All controllers, models, and DTOs include XML documentation comments that are automatically surfaced in the Swagger UI. The `GenerateDocumentationFile` MSBuild property is enabled in each `.csproj` so the XML docs are included at build time.

### Prerequisites

- .NET 8 SDK
- Node.js 18+
- Angular CLI (`npm install -g @angular/cli`)

### Run Locally

```bash
cd services/inventory-service
dotnet restore src/InventoryService.Api/InventoryService.Api.csproj
dotnet run --project src/InventoryService.Api/InventoryService.Api.csproj
```

The API will be available at `https://localhost:5001`. Visit `/swagger` for interactive API docs.

### Run Tests

```bash
dotnet test --verbosity normal
```

## Project Structure

| Artifact | Path | Description |
|----------|------|-------------|
| API source | `src/InventoryService.Api/` | .NET 8 Web API with EF Core + SQLite |
| Angular frontend | `client-app/` | Angular 17 standalone components |
| Dockerfile | `docker/Dockerfile` | Multi-stage build (Angular + .NET + runtime) |
| Helm chart | `helm/inventory-service/` | K8s deployment, service, network policy, HPA, service monitor |
| ArgoCD | `argocd/` | Application manifests for dev and staging |
| CI/CD | `.github/workflows/build-push.yaml` | Build, test, push to ECR |
| Tests | `tests/` | xUnit integration tests |

## Monolith Integration

The OrderManager monolith calls this service via HTTP instead of direct database access. Configure the monolith with the `InventoryService__BaseUrl` environment variable pointing to this service.

## Platform Conformance

All services conform to the platform standard defined in [platform-engineering-shared-services](https://github.com/Cognition-Partner-Workshops/platform-engineering-shared-services):
- Deploys into `decomposition-dev` / `decomposition-staging` namespaces
- Network policies: default-deny with explicit ingress from nginx and monitoring
- ServiceMonitor for Prometheus scraping
- HPA for auto-scaling in staging
- ArgoCD automated sync with prune and self-heal

## License

MIT
