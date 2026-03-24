# Contributing

Guidelines for developing and contributing to the OrderManager microservices.

## Development Environment

### Required Tools

| Tool | Version | Purpose |
|------|---------|---------|
| .NET SDK | 8.0 | Backend API development |
| Node.js | 20.x | Angular frontend builds |
| npm | 10.x+ | JavaScript package management |
| Docker | 24+ | Containerized builds and local testing |
| Helm | 3.x | Kubernetes deployments (optional) |

### IDE Recommendations

- **Visual Studio 2022** or **JetBrains Rider** for .NET development
- **VS Code** with the C# Dev Kit and Angular Language Service extensions

## Project Conventions

### Code Style

- **C#**: Follow the default .NET conventions. Nullable reference types are enabled (`<Nullable>enable</Nullable>`). Use XML doc comments (`///`) on all public types and members.
- **TypeScript/Angular**: Use standalone components with lazy-loaded routes. Follow the Angular style guide for file naming (`kebab-case`) and component structure.
- **Naming**: PascalCase for C# types and public members. camelCase for TypeScript/JSON properties.

### Architecture Patterns

Each microservice follows the same layered architecture:

```
ServiceName/
├── Controllers/    # HTTP API layer — thin, delegates to Services
├── Services/       # Business logic — all domain rules live here
├── Models/         # Domain entities — EF Core mapped classes
├── DTOs/           # Data transfer objects (optional, for cross-service use)
├── Data/           # DbContext, entity configuration, seed data
└── Program.cs      # Composition root — DI registration and middleware
```

**Guidelines:**
- Controllers should be thin — validate inputs, call a service method, return a result
- Business logic belongs in the `Services/` layer, not in controllers or EF configurations
- Each service owns its own database — no shared database between services
- Use DTOs when exposing data to other services to avoid coupling to internal models

### Database

- SQLite is used for simplicity; each service has its own `.db` file
- Entity configuration (keys, indexes, constraints) is defined in `DbContext.OnModelCreating`
- Seed data is applied at startup via `SeedData.Initialize()` — only runs when the table is empty

### Testing

- Tests use **xUnit** as the framework
- Use EF Core's **in-memory database provider** for unit tests
- Each test should create a fresh database context (via `Guid.NewGuid()` database names) for isolation
- Test file location: `tests/{ServiceName}.Api.Tests/`

```bash
# Run all tests for a service
cd inventory-service
dotnet test --verbosity normal
```

## Workflow

### Adding a New Service

1. Create a new directory at the repo root (e.g., `orders-service/`)
2. Scaffold the .NET project: `dotnet new webapi -n OrdersService.Api`
3. Follow the layered architecture pattern (Controllers → Services → Data → Models)
4. Add a `client-app/` directory with an Angular 17 SPA if the service has a UI
5. Add a `docker/Dockerfile` following the multi-stage build pattern from `inventory-service`
6. Add a Helm chart under `helm/{service-name}/` with environment-specific value overrides
7. Add a CI workflow under `.github/workflows/`
8. Update the root `README.md` Services table

### Adding a New API Endpoint

1. Add the route to the appropriate controller
2. Implement the business logic in the service layer
3. Add XML doc comments to the controller action and service method
4. Write unit tests covering the happy path and error cases
5. Verify the endpoint appears in Swagger UI

### Modifying the Data Model

1. Update the entity class in `Models/`
2. Update the `OnModelCreating` configuration if adding constraints or indexes
3. Update the `SeedData` class if the new field should be included in sample data
4. Update any DTOs that expose the entity
5. Run existing tests to check for regressions

## Docker

### Building Locally

```bash
cd inventory-service
docker build -f docker/Dockerfile -t inventory-service:local .
docker run -p 8080:8080 inventory-service:local
```

The multi-stage build produces a minimal Alpine-based image (~100 MB) containing both the .NET API and the pre-built Angular SPA served from `wwwroot/`.

### Image Tagging

- CI tags images with the commit SHA and `latest`
- Environment-specific tags (`dev-latest`, `staging-latest`) are set in Helm value overrides

## Kubernetes / Helm

### Value Overrides

| File | Use Case |
|------|----------|
| `values.yaml` | Base defaults |
| `values-dev.yaml` | Development — minimal resources, no persistence, verbose logging |
| `values-staging.yaml` | Staging — production-like with HPA enabled |

### Key Configuration

- **Health probes**: Liveness and readiness probes target `GET /health`
- **Environment variables**: Set via `env` in Helm values (e.g., `ASPNETCORE_ENVIRONMENT`, `ConnectionStrings__DefaultConnection`)
- **Persistence**: SQLite file stored on a PersistentVolume at `/data/` in production; ephemeral in dev
- **Autoscaling**: HPA scales on CPU utilization; disabled by default, enabled in staging

## CI/CD

GitHub Actions workflows are stored under each service's `.github/workflows/` directory. The pipeline:

1. Runs `dotnet test` to gate on quality
2. Authenticates to AWS via OIDC federation (no long-lived secrets)
3. Builds the Docker image
4. Pushes to Amazon ECR

Workflows trigger only when files in the respective service directory change, keeping builds fast and scoped.
