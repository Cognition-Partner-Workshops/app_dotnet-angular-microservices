// ---------------------------------------------------------------------------
// CustomerService.Api — Application Entry Point
//
// Configures and launches the Customer microservice, which exposes a REST API
// for managing customer records. Uses SQLite for persistence and seeds the
// database with sample data on first run.
// ---------------------------------------------------------------------------

using Microsoft.EntityFrameworkCore;
using CustomerService.Api.Data;

var builder = WebApplication.CreateBuilder(args);

// Register the SQLite-backed EF Core database context.
builder.Services.AddDbContext<CustomerDbContext>(options =>
    options.UseSqlite(builder.Configuration.GetConnectionString("DefaultConnection") ?? "Data Source=customers.db"));

// Register business-logic services with a scoped lifetime (one per HTTP request).
builder.Services.AddScoped<CustomerService.Api.Services.CustomerService>();

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

// Seed the database with initial customer records if the table is empty.
using (var scope = app.Services.CreateScope())
{
    var context = scope.ServiceProvider.GetRequiredService<CustomerDbContext>();
    SeedData.Initialize(context);
}

app.UseSwagger();
app.UseSwaggerUI();
app.UseCors();
app.MapControllers();
app.MapHealthChecks("/health");
app.Run();
