# app_dotnet-angular-microservices

> **Status:** This repository is a placeholder for the future microservices decomposition of the
> [OrderManager monolith](https://github.com/Cognition-Partner-Workshops/app_dotnet-angular-monolith).
> See the [decomposition plan](https://github.com/Cognition-Partner-Workshops/app_dotnet-angular-monolith-iac/blob/main/docs/decomposition-plan.md)
> in the IaC repo for the planned architecture and migration steps.

## Planned Services

| Service | Responsibility |
|---------|---------------|
| `product-service` | Product catalog management |
| `customer-service` | Customer profiles and addresses |
| `inventory-service` | Stock levels, reorder thresholds, warehouse locations |
| `order-service` | Order creation, status tracking, fulfillment |
| `api-gateway` | Single entry point for client requests; routes to backend microservices |
| `web-frontend` | Angular SPA served independently from the backend APIs |

## Related Repositories

- **[app_dotnet-angular-monolith](https://github.com/Cognition-Partner-Workshops/app_dotnet-angular-monolith)** — the current monolithic application (the "before" state)
- **[app_dotnet-angular-monolith-iac](https://github.com/Cognition-Partner-Workshops/app_dotnet-angular-monolith-iac)** — infrastructure-as-code for the monolith (Dockerfile, Helm chart, ArgoCD, CI/CD)
