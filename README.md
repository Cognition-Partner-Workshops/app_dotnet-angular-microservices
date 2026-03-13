# Microservices — Decomposed from OrderManager Monolith

This repository contains microservices extracted from the [OrderManager monolith](https://github.com/Cognition-Partner-Workshops/app_dotnet-angular-monolith).

## Customer Service

The **Customer Service** is the first domain decomposed from the monolith. It owns all customer data and exposes a REST API for CRUD operations.

### Technology Stack

- **Backend:** .NET 8 / ASP.NET Core Web API
- **Frontend:** Angular 17 (standalone components)
- **Database:** SQLite (per-service database: `customers.db`)
- **ORM:** Entity Framework Core 8

### How to Run

#### Prerequisites

- [.NET 8 SDK](https://dotnet.microsoft.com/download/dotnet/8.0)
- [Node.js 18+](https://nodejs.org/) (for the Angular frontend)

#### Backend

```bash
cd src/CustomerService.Api
dotnet restore
dotnet run
```

The API starts on `http://localhost:5101` by default (configure via `Properties/launchSettings.json` or `--urls` flag).

The database (`customers.db`) is auto-created and seeded on first run.

#### Frontend

```bash
cd src/CustomerService.Api/client-app
npm install
npx ng serve
```

The Angular dev server starts on `http://localhost:4200` and calls the Customer Service API at `http://localhost:5101`.

For production, build with `npx ng build` — output goes to `../wwwroot` and is served by the .NET backend as static files.

### API Endpoints

| Method | Endpoint              | Description                |
|--------|-----------------------|----------------------------|
| GET    | `/api/customers`      | List all customers         |
| GET    | `/api/customers/{id}` | Get customer by ID (returns `CustomerDto`) |
| POST   | `/api/customers`      | Create a new customer      |
| PUT    | `/api/customers/{id}` | Update an existing customer|
| DELETE | `/api/customers/{id}` | Delete a customer          |
| GET    | `/health`             | Health check endpoint      |
| GET    | `/swagger`            | Swagger/OpenAPI UI         |

### API Contracts

#### Customer (full entity)

```json
{
  "id": 1,
  "name": "Acme Corp",
  "email": "orders@acme.com",
  "phone": "555-0100",
  "address": "123 Main St",
  "city": "Springfield",
  "state": "IL",
  "zipCode": "62701",
  "createdAt": "2024-01-01T00:00:00Z"
}
```

#### CustomerDto (cross-service contract)

Used by other services (e.g., Orders) that need customer data for operations like shipping address lookup:

```json
{
  "id": 1,
  "name": "Acme Corp",
  "email": "orders@acme.com",
  "phone": "555-0100",
  "address": "123 Main St",
  "city": "Springfield",
  "state": "IL",
  "zipCode": "62701"
}
```

The `GET /api/customers/{id}` endpoint returns the `CustomerDto` format, which includes all fields needed by the Orders service for shipping address construction (`address`, `city`, `state`, `zipCode`).

### Database Setup

The service uses SQLite with Entity Framework Core. On first startup:

1. The database file `customers.db` is created automatically
2. The schema is applied via `EnsureCreated()`
3. Seed data is inserted (3 sample customers: Acme Corp, Globex Inc, Initech LLC)

No manual migration steps are required for local development.

### Decomposition Rationale

The Customers domain was chosen as the first extraction because:

1. **Cleanest boundary** — Customers has no outbound service-level dependencies on other domains
2. **Minimal coupling** — The only cross-domain references are:
   - `Customer.Orders` navigation property (inbound from Orders; removed in the microservice)
   - `OrderService.CreateOrderAsync` reads customer data for shipping address (remains in the monolith, should eventually call this microservice via HTTP)
   - `CustomerService.GetCustomerByIdAsync` did `.Include(c => c.Orders)` (removed in the microservice since Orders is a separate domain)

### Remaining Coupling Points in the Monolith

The monolith (`app_dotnet-angular-monolith`) still contains customer data because:

- **`OrderService.CreateOrderAsync`** uses `_context.Customers.FindAsync(customerId)` to look up the customer's shipping address. This should eventually be replaced with an HTTP call to this Customer Service API (`GET /api/customers/{id}`), or an event-driven approach where the Orders service maintains a local cache of customer addresses.
- **`Customer.Orders` navigation property** still exists in the monolith's `Customer` model because the Orders module uses it. This will be removed when the Orders domain is also extracted.

### Project Structure

```
src/CustomerService.Api/
├── Controllers/
│   └── CustomersController.cs    # REST API endpoints
├── Data/
│   ├── CustomerDbContext.cs       # EF Core DbContext (Customers only)
│   └── SeedData.cs                # Initial customer data
├── DTOs/
│   └── CustomerDto.cs             # Cross-service data transfer object
├── Models/
│   └── Customer.cs                # Customer entity (no Orders nav prop)
├── Services/
│   └── CustomerService.cs         # Business logic (CRUD)
├── client-app/                    # Angular 17 frontend
│   ├── src/
│   │   ├── app/
│   │   │   ├── modules/customers/ # Customer list component
│   │   │   ├── app.component.ts   # App shell
│   │   │   └── app.routes.ts      # Routing
│   │   ├── environments/          # API URL configuration
│   │   ├── index.html
│   │   └── main.ts
│   ├── angular.json
│   ├── package.json
│   └── tsconfig.json
├── Program.cs                     # App entry point & DI configuration
├── appsettings.json               # Configuration
└── CustomerService.Api.csproj     # Project file
```
