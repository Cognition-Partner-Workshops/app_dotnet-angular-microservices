# Microservices - Decomposed from OrderManager Monolith

This repository contains microservices extracted from the [OrderManager monolith](https://github.com/Cognition-Partner-Workshops/app_dotnet-angular-monolith). Each service has its own source code, Dockerfile, Helm chart, ArgoCD manifests, and CI/CD pipeline.

## Repository Structure

```
├── services/                    # Service source code
│   └── inventory-service/       # Inventory microservice (.NET 8 + Angular 17)
├── docker/                      # Dockerfiles per service
│   └── inventory-service/
├── helm/                        # Helm charts per service
│   └── inventory-service/
├── argocd/                      # ArgoCD Application manifests per service
│   └── inventory-service/
└── .github/workflows/           # CI/CD pipelines per service
```

## Services

### Inventory Service

Manages product inventory, stock levels, warehouse locations, and reorder thresholds.

**API Endpoints:**
- `GET /api/inventory` — List all inventory items
- `GET /api/inventory/product/{productId}` — Get inventory for a product
- `POST /api/inventory/product/{productId}/restock` — Restock a product
- `GET /api/inventory/low-stock` — List items below reorder level
- `POST /api/inventory/check-stock` — Check stock availability
- `POST /api/inventory/deduct-stock` — Deduct stock for an order
- `GET /health` — Health check

**Tech Stack:** .NET 8 Web API, Entity Framework Core (SQLite), Angular 17, xUnit

**Local Development:**
```bash
cd services/inventory-service
dotnet run --project src/InventoryService.Api/InventoryService.Api.csproj --urls "http://localhost:5001"
```

**Run Tests:**
```bash
cd services/inventory-service
dotnet test
```

## Deployment

- **ECR:** `599083837640.dkr.ecr.us-east-1.amazonaws.com/workshop/inventory-service`
- **Dev namespace:** `decomposition-dev` (inventory-service-dev.workshop.local)
- **Staging namespace:** `decomposition-staging` (inventory-service-staging.workshop.local)
- **GitOps:** ArgoCD auto-syncs from `helm/inventory-service/` with environment-specific value overrides
