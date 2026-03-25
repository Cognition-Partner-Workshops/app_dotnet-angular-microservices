# Inventory Service Microservice

A standalone .NET 8 + Angular 17 microservice decomposed from the [OrderManager monolith](https://github.com/Cognition-Partner-Workshops/app_dotnet-angular-monolith). Manages stock levels, warehouse locations, and reorder thresholds.

## Architecture

| Component | Technology |
|-----------|------------|
| **Backend** | .NET 8, C#, Entity Framework Core, SQLite |
| **Frontend** | Angular 17, TypeScript |
| **API** | RESTful with Swagger/OpenAPI |
| **Container** | Multi-stage Docker build (Node + .NET SDK + ASP.NET runtime) |
| **Orchestration** | Kubernetes (Helm chart, HPA, NetworkPolicy, ServiceMonitor) |
| **GitOps** | ArgoCD application manifests for dev and staging |
| **CI/CD** | GitHub Actions — build, test, push to ECR |

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/inventory` | List all inventory items |
| GET | `/api/inventory/product/{productId}` | Get inventory for a specific product |
| POST | `/api/inventory/product/{productId}/restock` | Restock a product |
| GET | `/api/inventory/low-stock` | List items at or below reorder level |
| GET | `/api/inventory/product/{productId}/check?quantity=N` | Check stock availability |
| POST | `/api/inventory/product/{productId}/deduct` | Deduct stock (used by monolith OrderService) |
| GET | `/health` | Health check endpoint |

## Getting Started

### Prerequisites
- .NET 8 SDK
- Node.js 18+
- Angular CLI (`npm install -g @angular/cli`)

### Run the application

```bash
dotnet restore src/InventoryService.Api/InventoryService.Api.csproj
cd client-app && npm install && cd ..
dotnet run --project src/InventoryService.Api/InventoryService.Api.csproj
```

The application will be available at `http://localhost:5000`.

### Run tests

```bash
dotnet test
```

## Project Structure

```
src/InventoryService.Api/         # .NET 8 Web API
  Controllers/                    # API controllers
  Models/                         # Entity models
  Services/                       # Business logic
  Data/                           # EF Core DbContext and seed data
tests/InventoryService.Api.Tests/ # Unit tests
client-app/                       # Angular 17 frontend
docker/Dockerfile                 # Multi-stage Docker build
helm/inventory-service/           # Helm chart
argocd/                           # ArgoCD application manifests
.github/workflows/                # CI/CD pipeline
```

## Platform Conformance

This service conforms to the [platform-engineering-shared-services](https://github.com/Cognition-Partner-Workshops/platform-engineering-shared-services) standard:
- Deploys into dedicated namespaces (`decomposition-dev`, `decomposition-staging`)
- Network policies restrict ingress to ingress-nginx and monitoring namespaces
- ServiceMonitor enables Prometheus scraping
- HPA provides auto-scaling in staging/production

## License

MIT
