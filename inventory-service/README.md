# Inventory Service

A standalone .NET 8 + Angular 17 microservice decomposed from the [OrderManager monolith](https://github.com/Cognition-Partner-Workshops/app_dotnet-angular-monolith). Manages stock levels, warehouse locations, and reorder operations.

## Architecture

This microservice owns the **Inventory** bounded context:

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/inventory` | GET | List all inventory items |
| `/api/inventory/product/{id}` | GET | Get inventory for a specific product |
| `/api/inventory/product/{id}/restock` | POST | Restock a product |
| `/api/inventory/product/{id}/deduct` | POST | Deduct stock (called by monolith) |
| `/api/inventory/low-stock` | GET | List items at or below reorder level |
| `/health` | GET | Health check |

## Tech Stack

- **Backend**: .NET 8, C#, Entity Framework Core, SQLite
- **Frontend**: Angular 17, TypeScript
- **API**: RESTful with Swagger/OpenAPI
- **Container**: Multi-stage Docker build (Alpine)
- **Orchestration**: Kubernetes (Helm chart included)
- **GitOps**: ArgoCD application manifests for dev/staging
- **CI/CD**: GitHub Actions (build, test, push to ECR)

## Getting Started

### Prerequisites
- .NET 8 SDK
- Node.js 18+
- Angular CLI (`npm install -g @angular/cli`)

### Run locally

```bash
# Restore and run the API
dotnet restore src/InventoryService.Api/InventoryService.Api.csproj
dotnet run --project src/InventoryService.Api/InventoryService.Api.csproj
```

The API will be available at `http://localhost:5000` with Swagger at `/swagger`.

### Run tests

```bash
dotnet test
```

## Deployment

### Docker

```bash
docker build -f docker/Dockerfile -t inventory-service:latest .
docker run -p 8080:8080 inventory-service:latest
```

### Kubernetes (Helm)

```bash
helm upgrade --install inventory-service helm/inventory-service \
  -f helm/inventory-service/values-dev.yaml \
  -n decomposition-dev
```

### ArgoCD

```bash
kubectl apply -f argocd/application-dev.yaml
```

## Platform Conformance

This service conforms to the [platform-engineering-shared-services](https://github.com/Cognition-Partner-Workshops/platform-engineering-shared-services) standard:

- Deploys into platform-provisioned namespaces (`decomposition-dev`, `decomposition-staging`)
- Uses the shared ingress-nginx controller
- Exposes Prometheus metrics via ServiceMonitor
- Follows default-deny network policies with explicit allow rules
- Images stored in platform-managed ECR repository
- GitOps delivery via ArgoCD

## License

MIT
