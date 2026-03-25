# Inventory Microservice

A standalone .NET 8 + Angular 17 microservice extracted from the OrderManager monolith. Manages stock levels, warehouse locations, and reorder alerts independently.

## Architecture

| Component | Description |
|-----------|-------------|
| **API** | .NET 8 Web API with EF Core, SQLite |
| **Frontend** | Angular 17 standalone components |
| **IaC** | Helm chart, ArgoCD manifests, Dockerfile |
| **CI/CD** | GitHub Actions — build, test, push to ECR |

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/inventory` | List all inventory items |
| GET | `/api/inventory/product/{id}` | Get inventory for a product |
| POST | `/api/inventory/product/{id}/restock` | Restock a product |
| POST | `/api/inventory/product/{id}/deduct` | Deduct stock (called by monolith) |
| GET | `/api/inventory/low-stock` | List items below reorder level |
| GET | `/health` | Health check |

## Getting Started

### Prerequisites
- .NET 8 SDK
- Node.js 18+
- Angular CLI (`npm install -g @angular/cli`)

### Run the application

```bash
# Restore .NET dependencies
dotnet restore

# Install Angular dependencies
cd client-app && npm install && cd ..

# Run the API
dotnet run --project src/InventoryService.Api/InventoryService.Api.csproj
```

The service will be available at `https://localhost:5001`.

### Run tests

```bash
dotnet test
```

## Deployment

### Docker

```bash
docker build -f docker/Dockerfile -t inventory-service .
docker run -p 8080:8080 inventory-service
```

### Kubernetes (Helm)

```bash
helm install inventory-service helm/inventory-service -f helm/inventory-service/values-dev.yaml
```

### ArgoCD

Apply the ArgoCD application manifests:

```bash
kubectl apply -f argocd/application-dev.yaml
kubectl apply -f argocd/application-staging.yaml
```

## Platform Conformance

This service conforms to the [platform-engineering-shared-services](https://github.com/Cognition-Partner-Workshops/platform-engineering-shared-services) standard:
- Network policies restrict ingress to ingress-nginx and monitoring namespaces
- ServiceMonitor for Prometheus scraping
- HPA for horizontal autoscaling in staging
- Deploys to `decomposition-dev` and `decomposition-staging` namespaces

## License

MIT
