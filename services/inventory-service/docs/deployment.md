# Inventory Service Deployment Guide

## Table of Contents

- [Local Development](#local-development)
- [Docker](#docker)
- [Kubernetes with Helm](#kubernetes-with-helm)
- [ArgoCD GitOps](#argocd-gitops)
- [CI/CD Pipeline](#cicd-pipeline)
- [Environment Configuration](#environment-configuration)
- [Monitoring](#monitoring)
- [Troubleshooting](#troubleshooting)

## Local Development

### Prerequisites

- .NET 8 SDK
- Node.js 18+ (for Angular frontend)

### Start the service

```bash
cd services/inventory-service

# Restore dependencies
dotnet restore

# Run on custom port
dotnet run --project src/InventoryService.Api/InventoryService.Api.csproj --urls "http://localhost:5100"
```

The SQLite database file (`inventory.db`) is created automatically in the project directory on first run with seed data.

### Run with the monolith

```bash
# Terminal 1: Inventory service
cd services/inventory-service
dotnet run --project src/InventoryService.Api/InventoryService.Api.csproj --urls "http://localhost:5100"

# Terminal 2: OrderManager monolith
cd /path/to/app_dotnet-angular-monolith
dotnet run --project src/OrderManager.Api/OrderManager.Api.csproj --urls "http://localhost:5000"
```

The monolith's `appsettings.json` has `InventoryService:BaseUrl` defaulting to `http://localhost:5100`.

### Verify the integration

```bash
# Check inventory service directly
curl http://localhost:5100/api/inventory
curl http://localhost:5100/health

# Check via monolith proxy
curl http://localhost:5000/api/inventory

# Create an order (triggers stock check + deduction)
curl -X POST http://localhost:5000/api/orders \
  -H "Content-Type: application/json" \
  -d '{"customerId":1,"items":[{"productId":1,"quantity":2}]}'

# Verify stock was deducted
curl http://localhost:5100/api/inventory/product/1
```

## Docker

### Build the image

```bash
cd services/inventory-service
docker build -f docker/Dockerfile -t inventory-service:latest .
```

### Multi-stage build

The Dockerfile uses three stages for an optimized image:

| Stage | Base Image | Size | Purpose |
|-------|-----------|------|---------|
| `client-build` | `node:20-alpine` | ~180MB | Angular frontend build |
| `api-build` | `mcr.microsoft.com/dotnet/sdk:8.0` | ~740MB | .NET restore, build, publish |
| `runtime` | `mcr.microsoft.com/dotnet/aspnet:8.0-alpine` | ~100MB | Final runtime image |

### Run the container

```bash
# Basic run
docker run -p 5100:8080 inventory-service:latest

# With persistent storage
docker run -p 5100:8080 -v inventory-data:/data \
  -e ConnectionStrings__DefaultConnection="Data Source=/data/inventory.db" \
  inventory-service:latest
```

### Push to ECR

```bash
# Login to ECR
aws ecr get-login-password --region us-east-1 | \
  docker login --username AWS --password-stdin 599083837640.dkr.ecr.us-east-1.amazonaws.com

# Tag and push
docker tag inventory-service:latest \
  599083837640.dkr.ecr.us-east-1.amazonaws.com/workshop/inventory-service:latest
docker push \
  599083837640.dkr.ecr.us-east-1.amazonaws.com/workshop/inventory-service:latest
```

## Kubernetes with Helm

### Chart location

```
helm/inventory-service/
├── Chart.yaml
├── values.yaml           # Base values
├── values-dev.yaml       # Dev overrides
├── values-staging.yaml   # Staging overrides
└── templates/
    ├── _helpers.tpl
    ├── deployment.yaml
    ├── service.yaml
    ├── ingress.yaml
    ├── hpa.yaml
    ├── networkpolicy.yaml
    └── servicemonitor.yaml
```

### Validate the chart

```bash
helm lint helm/inventory-service
helm template inventory-service helm/inventory-service -f helm/inventory-service/values-dev.yaml
```

### Deploy to dev

```bash
helm upgrade --install inventory-service helm/inventory-service \
  -f helm/inventory-service/values.yaml \
  -f helm/inventory-service/values-dev.yaml \
  -n inventory-dev \
  --create-namespace
```

### Deploy to staging

```bash
helm upgrade --install inventory-service helm/inventory-service \
  -f helm/inventory-service/values.yaml \
  -f helm/inventory-service/values-staging.yaml \
  -n inventory-staging \
  --create-namespace
```

### Environment comparison

| Setting | Dev | Staging | Production (base) |
|---------|-----|---------|-------------------|
| Replicas | 1 | 2 | 1 |
| CPU request | 50m | 100m | 100m |
| Memory request | 128Mi | 256Mi | 256Mi |
| CPU limit | 250m | 500m | 500m |
| Memory limit | 256Mi | 512Mi | 512Mi |
| Autoscaling | Disabled | Enabled (2-4, 75% CPU) | Disabled |
| Persistence | Disabled | Default (1Gi gp2) | Enabled (1Gi gp2) |
| Ingress host | `inventory-dev.workshop.local` | `inventory-staging.workshop.local` | `inventory.workshop.local` |
| .NET environment | Development | Production | Production |

### Kubernetes resources created

| Resource | Description |
|----------|-------------|
| **Deployment** | Runs the container with health probes, resource limits, and environment variables |
| **Service** | ClusterIP service on port 80 forwarding to container port 8080 |
| **Ingress** | NGINX ingress with cert-manager TLS annotations |
| **HPA** | Horizontal Pod Autoscaler (when `autoscaling.enabled: true`) |
| **NetworkPolicy** | Default-deny ingress with explicit allow from namespace |
| **ServiceMonitor** | Prometheus scrape configuration for `/metrics` on port 8080 |

### Override values at deploy time

```bash
# Override image tag
helm upgrade --install inventory-service helm/inventory-service \
  --set image.tag=abc123 \
  -n inventory-dev

# Override replica count
helm upgrade --install inventory-service helm/inventory-service \
  --set replicaCount=3 \
  -n inventory-staging
```

## ArgoCD GitOps

### Application manifests

```
argocd/
├── application-dev.yaml       # Dev environment
└── application-staging.yaml   # Staging environment
```

Both applications are configured with:
- **Automated sync** with self-healing and pruning
- **Source**: This Git repository, pointing to the Helm chart path
- **Values files**: Base `values.yaml` + environment-specific overrides

### Apply ArgoCD applications

```bash
# Apply to the ArgoCD namespace
kubectl apply -f argocd/application-dev.yaml -n argocd
kubectl apply -f argocd/application-staging.yaml -n argocd
```

### Sync manually

```bash
argocd app sync inventory-service-dev
argocd app sync inventory-service-staging
```

### GitOps workflow

1. Developer pushes code changes to `main`
2. CI pipeline builds, tests, and pushes Docker image to ECR
3. Update the image tag in `values-dev.yaml` or `values-staging.yaml`
4. ArgoCD detects the change and auto-syncs the deployment
5. Kubernetes rolls out the new version with zero-downtime

## CI/CD Pipeline

### Pipeline file

The GitHub Actions workflow is defined in `ci/build-push.yaml` (reference) and `.github/workflows/build-push.yaml` (active).

### Triggers

| Event | Condition | Jobs Run |
|-------|-----------|----------|
| Push to `main` | Files changed under `services/inventory-service/` | `build-and-test` + `build-and-push` |
| Pull request to `main` | Files changed under `services/inventory-service/` | `build-and-test` only |

### Job: build-and-test

1. Checkout code
2. Setup .NET 8 SDK
3. `dotnet restore`
4. `dotnet build --no-restore --configuration Release`
5. `dotnet test --no-build --configuration Release`

### Job: build-and-push (main branch only)

1. Checkout code
2. Configure AWS credentials via OIDC (`arn:aws:iam::599083837640:role/github-actions-ecr-push`)
3. Login to Amazon ECR
4. Build Docker image with tags: `{git-sha}` and `latest`
5. Push both tags to ECR

### ECR repository

```
Registry:   599083837640.dkr.ecr.us-east-1.amazonaws.com
Repository: workshop/inventory-service
Region:     us-east-1
```

## Environment Configuration

### Application settings

| Setting | Description | Default |
|---------|-------------|---------|
| `ConnectionStrings:DefaultConnection` | SQLite connection string | `Data Source=inventory.db` |
| `Logging:LogLevel:Default` | Default log level | `Information` |
| `Logging:LogLevel:Microsoft.AspNetCore` | ASP.NET Core log level | `Warning` |

### Environment variables for Kubernetes

| Variable | Description | Dev | Staging/Production |
|----------|-------------|-----|-------------------|
| `ASPNETCORE_ENVIRONMENT` | .NET environment | `Development` | `Production` |
| `ConnectionStrings__DefaultConnection` | DB connection | `Data Source=inventory.db` | `Data Source=/data/inventory.db` |
| `ASPNETCORE_URLS` | Listening URL | `http://+:8080` | `http://+:8080` |

### Configuration hierarchy

ASP.NET Core loads configuration in this order (later sources override earlier):
1. `appsettings.json`
2. `appsettings.{Environment}.json`
3. Environment variables
4. Command-line arguments

## Monitoring

### Health checks

| Endpoint | Purpose | Used By |
|----------|---------|---------|
| `GET /health` | Application health | Kubernetes liveness/readiness probes |

### Prometheus metrics

The Helm chart includes a `ServiceMonitor` that configures Prometheus to scrape:
- **Path**: `/metrics`
- **Port**: 8080
- **Interval**: Default Prometheus scrape interval

### Logging

The service uses ASP.NET Core's built-in logging with structured output:
- **Info**: Database operations, startup messages
- **Warning**: Static files not found, deprecation notices
- **Error**: Unhandled exceptions, database errors

In Kubernetes, logs are written to stdout and collected by the cluster's log aggregator (e.g., Fluentd, Loki).

## Troubleshooting

### Service won't start

```bash
# Check if port is in use
lsof -i :5100

# Check .NET SDK version
dotnet --version  # Should be 8.0.x

# Check database file permissions
ls -la src/InventoryService.Api/inventory.db
```

### Database issues

```bash
# Reset the database (delete and let it re-seed)
rm src/InventoryService.Api/inventory.db*
dotnet run --project src/InventoryService.Api/InventoryService.Api.csproj
```

### Connection refused from monolith

1. Verify inventory service is running: `curl http://localhost:5100/health`
2. Check monolith's `appsettings.json` has correct `InventoryService:BaseUrl`
3. Check for firewall/network policy issues in Kubernetes

### Kubernetes pod not starting

```bash
# Check pod status
kubectl get pods -n inventory-dev -l app=inventory-service

# Check pod logs
kubectl logs -n inventory-dev -l app=inventory-service

# Check events
kubectl describe pod -n inventory-dev -l app=inventory-service

# Check if image exists in ECR
aws ecr describe-images --repository-name workshop/inventory-service --region us-east-1
```

### Helm deployment issues

```bash
# Check release status
helm status inventory-service -n inventory-dev

# Check release history
helm history inventory-service -n inventory-dev

# Rollback to previous version
helm rollback inventory-service -n inventory-dev
```
