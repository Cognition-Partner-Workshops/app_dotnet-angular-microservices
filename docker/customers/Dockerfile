# Stage 1: Build .NET API
FROM mcr.microsoft.com/dotnet/sdk:8.0 AS build
WORKDIR /src
COPY services/customers/Customers.sln ./
COPY services/customers/src/Customers.Api/Customers.Api.csproj src/Customers.Api/
RUN dotnet restore
COPY services/customers/ .
RUN dotnet publish src/Customers.Api/Customers.Api.csproj -c Release -o /app/publish

# Stage 2: Runtime
FROM mcr.microsoft.com/dotnet/aspnet:8.0-alpine AS runtime
WORKDIR /app
COPY --from=build /app/publish .
EXPOSE 8080
ENV ASPNETCORE_URLS=http://+:8080
ENTRYPOINT ["dotnet", "Customers.Api.dll"]
