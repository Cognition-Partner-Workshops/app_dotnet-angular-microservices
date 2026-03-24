# Customer Service

A microservice responsible for managing customer records. Built with .NET 8 (ASP.NET Core) and an Angular 17 frontend. Provides full CRUD operations and exposes DTOs for cross-service communication (e.g., shipping address lookups from an Orders service).

## Overview

The Customer Service manages the customer directory for the OrderManager platform:

- **Full CRUD** — create, read, update, and delete customer records
- **DTO mapping** — exposes `CustomerDto` for lightweight cross-service responses
- **Unique email enforcement** — database-level unique index on customer email
- **Seed data** — auto-populates sample customers on first run

## Architecture

```
┌────────────────────────────────────────────────┐
│              Customer Service                   │
│                                                 │
│  ┌──────────────┐     ┌─────────────────────┐  │
│  │ Angular 17   │────▶│  .NET 8 Web API     │  │
│  │ SPA (:4200)  │     │  (Kestrel :5101)    │  │
│  └──────────────┘     └──────────┬──────────┘  │
│                                  │              │
│                       ┌──────────▼──────────┐  │
│                       │  SQLite             │  │
│                       │  (customers.db)     │  │
│                       └─────────────────────┘  │
└────────────────────────────────────────────────┘
```

### Backend Layers

| Layer | Location | Responsibility |
|-------|----------|---------------|
| Controllers | `Controllers/` | HTTP routing, request/response mapping, DTO conversion |
| Services | `Services/` | Business logic (CRUD operations, validation) |
| Data | `Data/` | EF Core DbContext, entity configuration, seed data |
| Models | `Models/` | Domain entities (`Customer`) |
| DTOs | `DTOs/` | Data transfer objects for cross-service communication |

### Frontend

The Angular SPA uses standalone components with lazy-loaded routes:

| Route | Component | Description |
|-------|-----------|-------------|
| `/customers` | `CustomerListComponent` | Displays a table of all customers with name, email, phone, and location |

## Getting Started

### Prerequisites

- [.NET 8 SDK](https://dotnet.microsoft.com/download/dotnet/8.0)
- [Node.js 20](https://nodejs.org/) and npm

### Run the API

```bash
dotnet run
# API available at http://localhost:5101
# Swagger UI at http://localhost:5101/swagger
```

The database is automatically created and seeded with sample data on first run.

### Run the Angular Frontend

```bash
cd client-app
npm install
npm start
# App available at http://localhost:4200
```

## API Reference

Base URL: `http://localhost:5101`

### List All Customers

```
GET /api/customers
```

**Response** `200 OK`

```json
[
  {
    "id": 1,
    "name": "Acme Corp",
    "email": "orders@acme.com",
    "phone": "555-0100",
    "address": "123 Main St",
    "city": "Springfield",
    "state": "IL",
    "zipCode": "62701",
    "createdAt": "2025-01-15T10:30:00Z"
  }
]
```

### Get Customer by ID

```
GET /api/customers/{id}
```

**Response** `200 OK` — returns a `CustomerDto` (without `createdAt`), or `404 Not Found`.

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

### Create a Customer

```
POST /api/customers
Content-Type: application/json

{
  "name": "New Customer",
  "email": "contact@newcustomer.com",
  "phone": "555-0400",
  "address": "101 Elm St",
  "city": "Metropolis",
  "state": "NY",
  "zipCode": "10001"
}
```

**Response** `201 Created` — returns the created customer with a `Location` header.

### Update a Customer

```
PUT /api/customers/{id}
Content-Type: application/json

{
  "name": "Updated Name",
  "email": "new@email.com",
  "phone": "555-9999",
  "address": "200 Oak Ave",
  "city": "Gotham",
  "state": "NJ",
  "zipCode": "07001"
}
```

**Response** `200 OK` — returns the updated customer, or `404 Not Found`.

### Delete a Customer

```
DELETE /api/customers/{id}
```

**Response** `204 No Content` on success, or `404 Not Found`.

### Health Check

```
GET /health
```

Returns `200 OK` with `Healthy` status. Used by Kubernetes liveness and readiness probes.

## Data Model

### Customer

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| `Id` | `int` | PK, auto-generated | Unique identifier |
| `Name` | `string` | Required, max 200 | Full name or company name |
| `Email` | `string` | Required, max 200, unique index | Email address |
| `Phone` | `string` | — | Phone number |
| `Address` | `string` | — | Street address |
| `City` | `string` | — | City name |
| `State` | `string` | — | State or province abbreviation |
| `ZipCode` | `string` | — | Postal / ZIP code |
| `CreatedAt` | `DateTime` | Default: UTC now | Record creation timestamp |

### CustomerDto

A lightweight projection of `Customer` used for cross-service responses. Contains all fields except `CreatedAt`. Used by the `GET /api/customers/{id}` endpoint to provide data suitable for consumption by other services (e.g., an Orders service looking up a shipping address).

## Seed Data

On first run, the database is populated with three sample customers:

| Id | Name | Email | Phone | City | State |
|----|------|-------|-------|------|-------|
| 1 | Acme Corp | orders@acme.com | 555-0100 | Springfield | IL |
| 2 | Globex Inc | purchasing@globex.com | 555-0200 | Shelbyville | IL |
| 3 | Initech LLC | supplies@initech.com | 555-0300 | Capital City | IL |

## Configuration

### appsettings.json

```json
{
  "ConnectionStrings": {
    "DefaultConnection": "Data Source=customers.db"
  }
}
```

The connection string can be overridden via environment variables (e.g., `ConnectionStrings__DefaultConnection`) for container deployments.

### appsettings.Development.json

Adds verbose logging when running in the `Development` environment (the default when using `dotnet run`).
