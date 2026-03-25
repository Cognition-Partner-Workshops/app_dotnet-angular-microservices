# Stage 1: Build .NET API
FROM mcr.microsoft.com/dotnet/sdk:8.0 AS build
WORKDIR /src
COPY services/products/Products.sln ./
COPY services/products/src/Products.Api/Products.Api.csproj src/Products.Api/
RUN dotnet restore
COPY services/products/ .
RUN dotnet publish src/Products.Api/Products.Api.csproj -c Release -o /app/publish

# Stage 2: Runtime
FROM mcr.microsoft.com/dotnet/aspnet:8.0-alpine AS runtime
WORKDIR /app
COPY --from=build /app/publish .
EXPOSE 8080
ENV ASPNETCORE_URLS=http://+:8080
ENTRYPOINT ["dotnet", "Products.Api.dll"]
