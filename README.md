# Microservices — Decomposed from OrderManager Monolith

This repository contains microservices extracted from the [OrderManager monolith](https://github.com/Cognition-Partner-Workshops/app_dotnet-angular-monolith). Each service is self-contained with its own API, frontend, Dockerfile, Helm chart, ArgoCD manifests, and CI/CD pipeline.

## Services

| Service | Description | Tech Stack |
|---------|-------------|------------|
| [inventory-service](services/inventory-service/) | Stock levels, warehouse locations, reorder management | .NET 8, Angular 17, EF Core, SQLite |

## Repository Structure

```
services/
└── inventory-service/
    ├── src/InventoryService.Api/    # .NET 8 Web API
    ├── tests/                       # xUnit tests
    ├── client-app/                  # Angular 17 frontend
    ├── docker/Dockerfile            # Multi-stage Docker build
    ├── helm/inventory-service/      # Helm chart
    ├── argocd/                      # ArgoCD application manifests
    └── InventoryService.sln
.github/workflows/                   # CI/CD pipelines
```

## Platform Conformance

All services conform to the [platform-engineering-shared-services](https://github.com/Cognition-Partner-Workshops/platform-engineering-shared-services) standard and follow IaC patterns from [app_dotnet-angular-monolith-iac](https://github.com/Cognition-Partner-Workshops/app_dotnet-angular-monolith-iac).
