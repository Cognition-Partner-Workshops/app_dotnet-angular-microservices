using Microsoft.AspNetCore.Mvc;
using CustomerService.Api.Models;
using CustomerService.Api.DTOs;

namespace CustomerService.Api.Controllers;

/// <summary>
/// REST API controller for managing customers.
/// Provides full CRUD endpoints and maps entities to DTOs for cross-service responses.
/// </summary>
[ApiController]
[Route("api/[controller]")]
public class CustomersController : ControllerBase
{
    private readonly Services.CustomerService _customerService;

    /// <summary>
    /// Initializes a new instance of <see cref="CustomersController"/>.
    /// </summary>
    /// <param name="customerService">The customer business-logic service.</param>
    public CustomersController(Services.CustomerService customerService)
    {
        _customerService = customerService;
    }

    /// <summary>
    /// Retrieves all customers in the system.
    /// </summary>
    /// <returns>A list of all <see cref="Customer"/> records.</returns>
    [HttpGet]
    public async Task<IActionResult> GetAll()
    {
        var customers = await _customerService.GetAllCustomersAsync();
        return Ok(customers);
    }

    /// <summary>
    /// Retrieves a single customer by their identifier, mapped to a <see cref="CustomerDto"/>.
    /// </summary>
    /// <param name="id">The customer identifier.</param>
    /// <returns>The matching <see cref="CustomerDto"/>, or 404 if not found.</returns>
    [HttpGet("{id}")]
    public async Task<IActionResult> GetById(int id)
    {
        var customer = await _customerService.GetCustomerByIdAsync(id);
        if (customer is null) return NotFound();

        var dto = MapToDto(customer);
        return Ok(dto);
    }

    /// <summary>
    /// Creates a new customer record.
    /// </summary>
    /// <param name="customer">The customer data to persist.</param>
    /// <returns>The created customer with a 201 Created response and a Location header.</returns>
    [HttpPost]
    public async Task<IActionResult> Create([FromBody] Customer customer)
    {
        var created = await _customerService.CreateCustomerAsync(customer);
        return CreatedAtAction(nameof(GetById), new { id = created.Id }, created);
    }

    /// <summary>
    /// Updates an existing customer's details.
    /// </summary>
    /// <param name="id">The identifier of the customer to update.</param>
    /// <param name="customer">The updated customer data.</param>
    /// <returns>The updated customer on success, or 404 if not found.</returns>
    [HttpPut("{id}")]
    public async Task<IActionResult> Update(int id, [FromBody] Customer customer)
    {
        var updated = await _customerService.UpdateCustomerAsync(id, customer);
        return updated is null ? NotFound() : Ok(updated);
    }

    /// <summary>
    /// Deletes a customer by their identifier.
    /// </summary>
    /// <param name="id">The identifier of the customer to delete.</param>
    /// <returns>204 No Content on success, or 404 if the customer was not found.</returns>
    [HttpDelete("{id}")]
    public async Task<IActionResult> Delete(int id)
    {
        var deleted = await _customerService.DeleteCustomerAsync(id);
        return deleted ? NoContent() : NotFound();
    }

    /// <summary>
    /// Maps a <see cref="Customer"/> entity to a <see cref="CustomerDto"/> for API responses
    /// that are consumed by other microservices.
    /// </summary>
    /// <param name="customer">The source customer entity.</param>
    /// <returns>A <see cref="CustomerDto"/> containing the mapped fields.</returns>
    private static CustomerDto MapToDto(Customer customer) => new()
    {
        Id = customer.Id,
        Name = customer.Name,
        Email = customer.Email,
        Phone = customer.Phone,
        Address = customer.Address,
        City = customer.City,
        State = customer.State,
        ZipCode = customer.ZipCode
    };
}
