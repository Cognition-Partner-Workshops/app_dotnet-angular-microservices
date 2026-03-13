using Microsoft.EntityFrameworkCore;
using CustomerService.Api.Data;
using CustomerService.Api.Models;

namespace CustomerService.Api.Services;

/// <summary>
/// Core business-logic service for customer management.
/// Encapsulates all CRUD operations against the customer database.
/// </summary>
public class CustomerService
{
    private readonly CustomerDbContext _context;

    /// <summary>
    /// Initializes a new instance of <see cref="CustomerService"/>.
    /// </summary>
    /// <param name="context">The database context used for customer data access.</param>
    public CustomerService(CustomerDbContext context)
    {
        _context = context;
    }

    /// <summary>
    /// Retrieves all customer records from the database.
    /// </summary>
    /// <returns>A list of every <see cref="Customer"/> in the system.</returns>
    public async Task<List<Customer>> GetAllCustomersAsync()
    {
        return await _context.Customers.ToListAsync();
    }

    /// <summary>
    /// Looks up a customer by their unique identifier.
    /// </summary>
    /// <param name="id">The customer identifier to search for.</param>
    /// <returns>The matching <see cref="Customer"/>, or <c>null</c> if not found.</returns>
    public async Task<Customer?> GetCustomerByIdAsync(int id)
    {
        return await _context.Customers.FirstOrDefaultAsync(c => c.Id == id);
    }

    /// <summary>
    /// Persists a new customer record to the database.
    /// </summary>
    /// <param name="customer">The customer entity to create.</param>
    /// <returns>The created <see cref="Customer"/> with its generated identifier.</returns>
    public async Task<Customer> CreateCustomerAsync(Customer customer)
    {
        _context.Customers.Add(customer);
        await _context.SaveChangesAsync();
        return customer;
    }

    /// <summary>
    /// Updates an existing customer's mutable fields (name, email, phone, and address details).
    /// </summary>
    /// <param name="id">The identifier of the customer to update.</param>
    /// <param name="updated">An object containing the new field values.</param>
    /// <returns>The updated <see cref="Customer"/>, or <c>null</c> if no customer with the given <paramref name="id"/> exists.</returns>
    public async Task<Customer?> UpdateCustomerAsync(int id, Customer updated)
    {
        var customer = await _context.Customers.FindAsync(id);
        if (customer is null) return null;

        customer.Name = updated.Name;
        customer.Email = updated.Email;
        customer.Phone = updated.Phone;
        customer.Address = updated.Address;
        customer.City = updated.City;
        customer.State = updated.State;
        customer.ZipCode = updated.ZipCode;

        await _context.SaveChangesAsync();
        return customer;
    }

    /// <summary>
    /// Deletes a customer record from the database.
    /// </summary>
    /// <param name="id">The identifier of the customer to remove.</param>
    /// <returns><c>true</c> if the customer was found and deleted; <c>false</c> otherwise.</returns>
    public async Task<bool> DeleteCustomerAsync(int id)
    {
        var customer = await _context.Customers.FindAsync(id);
        if (customer is null) return false;

        _context.Customers.Remove(customer);
        await _context.SaveChangesAsync();
        return true;
    }
}
