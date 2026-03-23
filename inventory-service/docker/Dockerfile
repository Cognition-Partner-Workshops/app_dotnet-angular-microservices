# =============================================================================
# Multi-stage Dockerfile for the Inventory Service
#
# Stages:
#   1. client-build  – Installs Node 20 dependencies and compiles the Angular
#                      17 SPA into static assets.
#   2. api-build     – Restores NuGet packages, copies the compiled Angular
#                      output into wwwroot, and publishes the .NET 8 API.
#   3. runtime       – Minimal ASP.NET Core Alpine image that serves both the
#                      API and the embedded SPA.
# =============================================================================

# ---------------------------------------------------------------------------
# Stage 1: Build the Angular client application
# ---------------------------------------------------------------------------
FROM node:20-alpine AS client-build
WORKDIR /app/client
# Copy only package manifests first to leverage Docker layer caching for npm ci.
COPY client-app/package*.json ./
RUN npm ci
# Copy the full client source and build the production bundle.
COPY client-app/ ./
RUN npm run build

# ---------------------------------------------------------------------------
# Stage 2: Build the .NET 8 Web API
# ---------------------------------------------------------------------------
FROM mcr.microsoft.com/dotnet/sdk:8.0 AS api-build
WORKDIR /src
# Copy solution and project files first for NuGet restore caching.
COPY InventoryService.sln ./
COPY src/InventoryService.Api/InventoryService.Api.csproj src/InventoryService.Api/
COPY tests/InventoryService.Api.Tests/InventoryService.Api.Tests.csproj tests/InventoryService.Api.Tests/
RUN dotnet restore
# Copy the remaining source code.
COPY . .
# Embed the compiled Angular SPA into the API's static-files directory.
COPY --from=client-build /app/client/dist/inventory-service-client/browser src/InventoryService.Api/wwwroot/
# Publish the API in Release configuration.
RUN dotnet publish src/InventoryService.Api/InventoryService.Api.csproj -c Release -o /app/publish

# ---------------------------------------------------------------------------
# Stage 3: Lightweight runtime image
# ---------------------------------------------------------------------------
FROM mcr.microsoft.com/dotnet/aspnet:8.0-alpine AS runtime
WORKDIR /app
# Copy the published output from the build stage.
COPY --from=api-build /app/publish .
# Expose the HTTP port used by Kestrel.
EXPOSE 8080
# Tell ASP.NET Core to listen on all interfaces at port 8080.
ENV ASPNETCORE_URLS=http://+:8080
# Launch the Inventory Service API.
ENTRYPOINT ["dotnet", "InventoryService.Api.dll"]
