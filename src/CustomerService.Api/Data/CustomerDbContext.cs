using Microsoft.EntityFrameworkCore;
using CustomerService.Api.Models;

namespace CustomerService.Api.Data;

/// <summary>
/// Entity Framework Core database context for the Customer microservice.
/// Manages persistence of <see cref="Customer"/> entities using SQLite.
/// </summary>
public class CustomerDbContext : DbContext
{
    /// <summary>
    /// Initializes a new instance of <see cref="CustomerDbContext"/> with the specified options.
    /// </summary>
    /// <param name="options">Database context configuration options (connection string, provider, etc.).</param>
    public CustomerDbContext(DbContextOptions<CustomerDbContext> options) : base(options) { }

    /// <summary>
    /// Gets the set of customer records stored in the database.
    /// </summary>
    public DbSet<Customer> Customers => Set<Customer>();

    /// <summary>
    /// Configures the entity model for <see cref="Customer"/>, including primary key,
    /// required fields, max lengths, and a unique index on <c>Email</c>.
    /// </summary>
    /// <param name="modelBuilder">The builder used to construct the model for this context.</param>
    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        modelBuilder.Entity<Customer>(entity =>
        {
            entity.HasKey(e => e.Id);
            entity.Property(e => e.Name).IsRequired().HasMaxLength(200);
            entity.Property(e => e.Email).IsRequired().HasMaxLength(200);
            entity.HasIndex(e => e.Email).IsUnique();
        });
    }
}
