using Microsoft.EntityFrameworkCore;
using InventoryService.Api.Models;

namespace InventoryService.Api.Data;

/// <summary>
/// Entity Framework Core database context for the Inventory microservice.
/// Manages persistence of <see cref="InventoryItem"/> entities using SQLite.
/// </summary>
public class InventoryDbContext : DbContext
{
    /// <summary>
    /// Initializes a new instance of <see cref="InventoryDbContext"/> with the specified options.
    /// </summary>
    /// <param name="options">Database context configuration options (connection string, provider, etc.).</param>
    public InventoryDbContext(DbContextOptions<InventoryDbContext> options) : base(options) { }

    /// <summary>
    /// Gets the set of inventory items stored in the database.
    /// </summary>
    public DbSet<InventoryItem> InventoryItems => Set<InventoryItem>();

    /// <summary>
    /// Configures the entity model for <see cref="InventoryItem"/>, including primary key,
    /// unique index on <c>ProductId</c>, and column constraints.
    /// </summary>
    /// <param name="modelBuilder">The builder used to construct the model for this context.</param>
    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        modelBuilder.Entity<InventoryItem>(entity =>
        {
            entity.HasKey(e => e.Id);
            entity.HasIndex(e => e.ProductId).IsUnique();
            entity.Property(e => e.ProductName).IsRequired().HasMaxLength(200);
            entity.Property(e => e.ProductSku).IsRequired().HasMaxLength(50);
            entity.Property(e => e.WarehouseLocation).HasMaxLength(50);
        });
    }
}
