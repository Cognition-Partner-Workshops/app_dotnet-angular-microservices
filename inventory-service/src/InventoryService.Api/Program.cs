// ---------------------------------------------------------------------------
// InventoryService.Api — Application Entry Point
//
// Configures and launches the Inventory microservice, which exposes a REST API
// for managing product stock levels. Uses SQLite for persistence and seeds the
// database with sample data on first run.
// ---------------------------------------------------------------------------

using Microsoft.EntityFrameworkCore;
using InventoryService.Api.Data;
using InventoryService.Api.Services;

var builder = WebApplication.CreateBuilder(args);

// Register the SQLite-backed EF Core database context.
builder.Services.AddDbContext<InventoryDbContext>(options =>
    options.UseSqlite(builder.Configuration.GetConnectionString("DefaultConnection") ?? "Data Source=inventory.db"));

// Register business-logic services with a scoped lifetime (one per HTTP request).
builder.Services.AddScoped<InventoryItemService>();

// Configure MVC controllers with JSON options that handle circular references.
builder.Services.AddControllers().AddJsonOptions(options =>
    options.JsonSerializerOptions.ReferenceHandler = System.Text.Json.Serialization.ReferenceHandler.IgnoreCycles);

// Enable OpenAPI (Swagger) documentation generation.
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

// Allow any origin for CORS — suitable for development; restrict in production.
builder.Services.AddCors(options =>
    options.AddDefaultPolicy(policy => policy.AllowAnyOrigin().AllowAnyMethod().AllowAnyHeader()));

// Expose a /health endpoint for container orchestrator liveness probes.
builder.Services.AddHealthChecks();

var app = builder.Build();

// Seed the database with initial inventory items if the table is empty.
using (var scope = app.Services.CreateScope())
{
    var context = scope.ServiceProvider.GetRequiredService<InventoryDbContext>();
    SeedData.Initialize(context);
}

// Enable Swagger JSON endpoint and interactive Swagger UI.
app.UseSwagger();
app.UseSwaggerUI();
// Enable the default CORS policy configured above.
app.UseCors();
// Serve the embedded Angular SPA assets from wwwroot.
app.UseStaticFiles();
// Map attribute-routed API controllers.
app.MapControllers();
// Expose /health for Kubernetes liveness and readiness probes.
app.MapHealthChecks("/health");
// For any unmatched route, serve index.html so Angular handles client-side routing.
app.MapFallbackToFile("index.html");
// Start the Kestrel web server and begin accepting requests.
app.Run();
