# app_dotnet-angular-microservices

Microservices decomposed from the [OrderManager monolith](https://github.com/Cognition-Partner-Workshops/app_dotnet-angular-monolith). Each service is independently deployable with its own Helm chart, Dockerfile, ArgoCD manifests, and CI/CD pipeline.

## Services

| Service | Description | Port |
|---------|-------------|------|
| [inventory-service](services/inventory-service/) | Stock levels, warehouse locations, reorder management | 8080 |

## Platform Conformance

All services conform to the [platform-engineering-shared-services](https://github.com/Cognition-Partner-Workshops/platform-engineering-shared-services) standard:
- Kubernetes-native with Helm charts
- ArgoCD GitOps deployments
- Prometheus ServiceMonitor for observability
- Network policies for namespace isolation
- HPA for autoscaling
