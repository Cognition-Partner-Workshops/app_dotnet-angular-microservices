# Microservices — Decomposed from OrderManager Monolith

This repository contains microservices extracted from the [OrderManager monolith](https://github.com/Cognition-Partner-Workshops/app_dotnet-angular-monolith). Each service is a self-contained unit with its own API, database, frontend, infrastructure-as-code, and CI/CD pipeline.

## Services

| Service | Description | Tech Stack | Status |
|---------|-------------|-----------|--------|
| [**inventory-service**](services/inventory-service/) | Stock levels, restocking, low-stock alerts, availability checks | .NET 8 + Angular 17 + SQLite | Active |

## Repository Structure

```
app_dotnet-angular-microservices/
├── services/
│   └── inventory-service/          # Inventory microservice
│       ├── src/                    #   .NET 8 Web API
│       ├── tests/                  #   xUnit tests
│       ├── client-app/             #   Angular 17 frontend
│       ├── docker/                 #   Multi-stage Dockerfile
│       ├── helm/                   #   Kubernetes Helm chart
│       ├── argocd/                 #   ArgoCD application manifests
│       ├── ci/                     #   CI/CD pipeline definition
│       ├── docs/                   #   Detailed documentation
│       │   ├── api.md              #     API reference
│       │   ├── architecture.md     #     Architecture & design decisions
│       │   └── deployment.md       #     Deployment guide
│       └── README.md               #   Service documentation
├── .github/workflows/              # GitHub Actions CI/CD pipelines
└── README.md                       # This file
```

## Quick Start

### Prerequisites

- [.NET 8 SDK](https://dotnet.microsoft.com/download/dotnet/8.0)
- [Node.js 18+](https://nodejs.org/)
- [Docker](https://docs.docker.com/get-docker/) (optional)
- [Helm 3](https://helm.sh/docs/intro/install/) (optional)

### Run a service locally

```bash
cd services/inventory-service

# Restore dependencies and run
dotnet restore
dotnet run --project src/InventoryService.Api/InventoryService.Api.csproj --urls "http://localhost:5100"
```

### Run tests

```bash
cd services/inventory-service
dotnet test
```

### Run with the OrderManager monolith

```bash
# Terminal 1: Start inventory-service on port 5100
cd services/inventory-service
dotnet run --project src/InventoryService.Api/InventoryService.Api.csproj --urls "http://localhost:5100"

# Terminal 2: Start the monolith on port 5000
cd /path/to/app_dotnet-angular-monolith
dotnet run --project src/OrderManager.Api/OrderManager.Api.csproj --urls "http://localhost:5000"
```

The monolith's `InventoryService:BaseUrl` config defaults to `http://localhost:5100`.

## Platform Conformance

All services conform to the [platform-engineering-shared-services](https://github.com/Cognition-Partner-Workshops/platform-engineering-shared-services) standard:

- Namespace isolation with resource quotas
- Network policies (default-deny with explicit allow rules)
- Prometheus ServiceMonitor for metrics
- ArgoCD GitOps deployments
- NGINX Ingress with cert-manager TLS
- Horizontal Pod Autoscaler for production workloads

## CI/CD

Each service has its own GitHub Actions pipeline that:

1. **On pull requests**: Builds, restores, and runs tests
2. **On push to main**: Builds Docker image, pushes to Amazon ECR, triggers ArgoCD sync

Container images are pushed to `599083837640.dkr.ecr.us-east-1.amazonaws.com/workshop/{service-name}`.

## Adding a New Service

To add a new microservice to this repository:

1. Create `services/{service-name}/` with the standard structure:
   - `src/` — Application code
   - `tests/` — Test projects
   - `client-app/` — Frontend (if applicable)
   - `docker/Dockerfile` — Multi-stage build
   - `helm/{service-name}/` — Helm chart with `values.yaml`, `values-dev.yaml`, `values-staging.yaml`
   - `argocd/` — ArgoCD application manifests
   - `ci/build-push.yaml` — CI/CD pipeline reference
   - `docs/` — API reference, architecture, and deployment docs
   - `README.md` — Service documentation
2. Add a GitHub Actions workflow under `.github/workflows/` scoped to `services/{service-name}/**`
3. Update this README's service table
4. Create ArgoCD applications for dev and staging environments

## Integration with the Monolith

The monolith communicates with decomposed services via HTTP. For each service:
- The monolith registers an `HttpClient` with the service's base URL
- An interface (e.g., `IInventoryServiceClient`) abstracts the HTTP calls
- The monolith's controllers may act as BFF proxies, forwarding frontend requests to the microservice

See each service's documentation for specific integration details.

## Documentation

Each service includes comprehensive documentation in its `docs/` directory:

| Document | Description |
|----------|-------------|
| `README.md` | Getting started, project structure, quick reference |
| `docs/api.md` | Full API reference with request/response examples |
| `docs/architecture.md` | Architecture diagrams, design decisions, known limitations |
| `docs/deployment.md` | Local dev, Docker, Helm, ArgoCD, CI/CD, and troubleshooting |

## License

MIT
