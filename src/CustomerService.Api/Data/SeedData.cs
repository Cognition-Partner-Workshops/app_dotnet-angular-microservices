using CustomerService.Api.Models;

namespace CustomerService.Api.Data;

/// <summary>
/// Provides initial seed data for the customer database.
/// Populates the database with sample customer records on first run when no customers exist.
/// </summary>
public static class SeedData
{
    /// <summary>
    /// Seeds the database with a default set of customers if the table is empty.
    /// Ensures the database schema is created before attempting to insert data.
    /// </summary>
    /// <param name="context">The <see cref="CustomerDbContext"/> used to access and populate the database.</param>
    public static void Initialize(CustomerDbContext context)
    {
        context.Database.EnsureCreated();

        if (context.Customers.Any()) return;

        var customers = new[]
        {
            new Customer { Name = "Acme Corp", Email = "orders@acme.com", Phone = "555-0100", Address = "123 Main St", City = "Springfield", State = "IL", ZipCode = "62701" },
            new Customer { Name = "Globex Inc", Email = "purchasing@globex.com", Phone = "555-0200", Address = "456 Oak Ave", City = "Shelbyville", State = "IL", ZipCode = "62565" },
            new Customer { Name = "Initech LLC", Email = "supplies@initech.com", Phone = "555-0300", Address = "789 Pine Rd", City = "Capital City", State = "IL", ZipCode = "62702" },
        };
        context.Customers.AddRange(customers);
        context.SaveChanges();
    }
}
