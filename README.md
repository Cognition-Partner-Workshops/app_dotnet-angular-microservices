# OrderManager Microservices

A microservices architecture built with **.NET 8** (ASP.NET Core Web API) and **Angular 17**, decomposed from a monolithic order-management system. Each service owns its domain, data store, and frontend, and is independently deployable to Kubernetes via Helm and ArgoCD.

## Architecture Overview

```
┌─────────────────────────────────────────────────────────┐
│                    Kubernetes Cluster                    │
│                                                         │
│  ┌───────────────────────┐  ┌────────────────────────┐  │
│  │   Inventory Service   │  │   Customer Service     │  │
│  │  ┌─────────────────┐  │  │  ┌──────────────────┐  │  │
│  │  │  Angular 17 SPA │  │  │  │  Angular 17 SPA  │  │  │
│  │  └────────┬────────┘  │  │  └────────┬─────────┘  │  │
│  │           │           │  │           │            │  │
│  │  ┌────────▼────────┐  │  │  ┌────────▼─────────┐  │  │
│  │  │ .NET 8 Web API  │  │  │  │ .NET 8 Web API   │  │  │
│  │  └────────┬────────┘  │  │  └────────┬─────────┘  │  │
│  │           │           │  │           │            │  │
│  │  ┌────────▼────────┐  │  │  ┌────────▼─────────┐  │  │
│  │  │  SQLite (file)  │  │  │  │  SQLite (file)   │  │  │
│  │  └─────────────────┘  │  │  └──────────────────┘  │  │
│  └───────────────────────┘  └────────────────────────┘  │
└─────────────────────────────────────────────────────────┘
```

## Services

| Service | Domain | Port | Location |
|---------|--------|------|----------|
| [Inventory Service](./inventory-service/) | Product stock levels, restocking, low-stock alerts | 5000 | `inventory-service/` |
| [Customer Service](./src/CustomerService.Api/) | Customer CRUD, address lookup DTOs | 5101 | `src/CustomerService.Api/` |

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | .NET 8, ASP.NET Core, Entity Framework Core 8 |
| Frontend | Angular 17 (standalone components, lazy-loaded routes) |
| Database | SQLite (file-based, per-service) |
| Testing | xUnit, EF Core In-Memory provider |
| Containers | Multi-stage Docker builds (Node 20 + .NET 8 SDK → Alpine runtime) |
| Orchestration | Kubernetes, Helm 3, ArgoCD |
| CI/CD | GitHub Actions → Amazon ECR |
| Observability | Health checks (`/health`), Prometheus ServiceMonitor |
| API Docs | Swagger / OpenAPI (Swashbuckle) |

## Prerequisites

- [.NET 8 SDK](https://dotnet.microsoft.com/download/dotnet/8.0)
- [Node.js 20](https://nodejs.org/) and npm
- [Docker](https://docs.docker.com/get-docker/) (for containerized builds)
- [Helm 3](https://helm.sh/) (for Kubernetes deployments)

## Quick Start

### Inventory Service

```bash
# Start the backend API (includes Swagger UI at /swagger)
cd inventory-service
dotnet run --project src/InventoryService.Api

# In a separate terminal, start the Angular dev server
cd inventory-service/client-app
npm install
npm start
# Angular app available at http://localhost:4200
# API proxied to http://localhost:5000/api/inventory
```

### Customer Service

```bash
# Start the backend API
cd src/CustomerService.Api
dotnet run

# In a separate terminal, start the Angular dev server
cd src/CustomerService.Api/client-app
npm install
npm start
# Angular app available at http://localhost:4200
# API at http://localhost:5101/api/customers
```

### Run Tests

```bash
# Inventory Service unit tests
cd inventory-service
dotnet test --verbosity normal
```

### Docker Build

```bash
# Build the Inventory Service container (API + embedded Angular SPA)
cd inventory-service
docker build -f docker/Dockerfile -t inventory-service:local .
docker run -p 8080:8080 inventory-service:local
# App available at http://localhost:8080
```

## Project Structure

```
app_dotnet-angular-microservices/
├── inventory-service/                  # Inventory microservice (fully decomposed)
│   ├── src/InventoryService.Api/       #   .NET 8 Web API
│   │   ├── Controllers/               #     REST endpoint definitions
│   │   ├── Models/                     #     Domain entities
│   │   ├── Services/                   #     Business logic layer
│   │   ├── Data/                       #     EF Core DbContext & seed data
│   │   └── Program.cs                  #     Application entry point
│   ├── client-app/                     #   Angular 17 SPA
│   │   └── src/app/modules/inventory/  #     Inventory list & restock components
│   ├── tests/                          #   xUnit test project
│   ├── docker/Dockerfile               #   Multi-stage container build
│   ├── helm/inventory-service/         #   Helm chart (deployment, ingress, HPA, etc.)
│   ├── argocd/                         #   ArgoCD Application manifests
│   └── .github/workflows/             #   CI/CD pipeline (build → ECR push)
│
├── src/CustomerService.Api/            # Customer microservice
│   ├── Controllers/                    #   REST endpoint definitions
│   ├── Models/                         #   Domain entities
│   ├── DTOs/                           #   Data transfer objects for cross-service use
│   ├── Services/                       #   Business logic layer
│   ├── Data/                           #   EF Core DbContext & seed data
│   ├── client-app/                     #   Angular 17 SPA
│   └── Program.cs                      #   Application entry point
│
└── README.md
```

## API Reference

### Inventory Service — `http://localhost:5000`

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/inventory` | List all inventory items |
| `GET` | `/api/inventory/product/{productId}` | Get inventory for a specific product |
| `POST` | `/api/inventory/product/{productId}/restock` | Add stock (body: `{ "quantity": 25 }`) |
| `POST` | `/api/inventory/product/{productId}/deduct` | Remove stock (body: `{ "quantity": 10 }`) |
| `GET` | `/api/inventory/low-stock` | List items at or below reorder level |
| `GET` | `/health` | Health check endpoint |
| `GET` | `/swagger` | Interactive API documentation |

### Customer Service — `http://localhost:5101`

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/customers` | List all customers |
| `GET` | `/api/customers/{id}` | Get customer by ID (returns DTO) |
| `POST` | `/api/customers` | Create a new customer |
| `PUT` | `/api/customers/{id}` | Update an existing customer |
| `DELETE` | `/api/customers/{id}` | Delete a customer |
| `GET` | `/health` | Health check endpoint |
| `GET` | `/swagger` | Interactive API documentation |

## Deployment

Each service can be deployed independently to Kubernetes using Helm:

```bash
# Deploy Inventory Service to the dev namespace
helm upgrade --install inventory-service \
  inventory-service/helm/inventory-service \
  -f inventory-service/helm/inventory-service/values-dev.yaml \
  -n dev --create-namespace

# Deploy to staging (enables HPA with 2-4 replicas)
helm upgrade --install inventory-service \
  inventory-service/helm/inventory-service \
  -f inventory-service/helm/inventory-service/values-staging.yaml \
  -n staging --create-namespace
```

ArgoCD Application manifests are provided in `inventory-service/argocd/` for GitOps-driven deployments.

### CI/CD Pipeline

The GitHub Actions workflow (`.github/workflows/build-push.yaml`) triggers on pushes to `main` that modify `inventory-service/` files:

1. Runs the .NET unit test suite
2. Authenticates to AWS via OIDC federation
3. Builds a multi-stage Docker image (Angular SPA + .NET API)
4. Pushes to Amazon ECR tagged with the commit SHA and `latest`

## Seed Data

Both services seed their databases with sample records on first run:

**Inventory Items:** Widget A, Widget B, Gadget X, Gadget Y, Thingamajig (5 products with SKUs and warehouse locations)

**Customers:** Acme Corp, Globex Inc, Initech LLC (3 sample business customers with full address details)

## License

This project is provided as-is for workshop and demonstration purposes.
