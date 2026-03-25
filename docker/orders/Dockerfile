# Stage 1: Build .NET API
FROM mcr.microsoft.com/dotnet/sdk:8.0 AS build
WORKDIR /src
COPY services/orders/Orders.sln ./
COPY services/orders/src/Orders.Api/Orders.Api.csproj src/Orders.Api/
RUN dotnet restore
COPY services/orders/ .
RUN dotnet publish src/Orders.Api/Orders.Api.csproj -c Release -o /app/publish

# Stage 2: Runtime
FROM mcr.microsoft.com/dotnet/aspnet:8.0-alpine AS runtime
WORKDIR /app
COPY --from=build /app/publish .
EXPOSE 8080
ENV ASPNETCORE_URLS=http://+:8080
ENTRYPOINT ["dotnet", "Orders.Api.dll"]
