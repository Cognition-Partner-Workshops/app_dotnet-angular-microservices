using Microsoft.AspNetCore.Mvc;
using CustomerService.Api.Models;
using CustomerService.Api.DTOs;

namespace CustomerService.Api.Controllers;

[ApiController]
[Route("api/[controller]")]
public class CustomersController : ControllerBase
{
    private readonly Services.CustomerService _customerService;

    public CustomersController(Services.CustomerService customerService)
    {
        _customerService = customerService;
    }

    [HttpGet]
    public async Task<IActionResult> GetAll()
    {
        var customers = await _customerService.GetAllCustomersAsync();
        return Ok(customers);
    }

    [HttpGet("{id}")]
    public async Task<IActionResult> GetById(int id)
    {
        var customer = await _customerService.GetCustomerByIdAsync(id);
        if (customer is null) return NotFound();

        var dto = MapToDto(customer);
        return Ok(dto);
    }

    [HttpPost]
    public async Task<IActionResult> Create([FromBody] Customer customer)
    {
        var created = await _customerService.CreateCustomerAsync(customer);
        return CreatedAtAction(nameof(GetById), new { id = created.Id }, created);
    }

    [HttpPut("{id}")]
    public async Task<IActionResult> Update(int id, [FromBody] Customer customer)
    {
        var updated = await _customerService.UpdateCustomerAsync(id, customer);
        return updated is null ? NotFound() : Ok(updated);
    }

    [HttpDelete("{id}")]
    public async Task<IActionResult> Delete(int id)
    {
        var deleted = await _customerService.DeleteCustomerAsync(id);
        return deleted ? NoContent() : NotFound();
    }

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
