namespace CustomerService.Api.Models;

/// <summary>
/// Represents a customer entity with contact and address information.
/// </summary>
public class Customer
{
    /// <summary>
    /// Gets or sets the unique identifier for this customer.
    /// </summary>
    public int Id { get; set; }

    /// <summary>
    /// Gets or sets the customer's full name or company name.
    /// </summary>
    public string Name { get; set; } = string.Empty;

    /// <summary>
    /// Gets or sets the customer's email address. Must be unique across all customers.
    /// </summary>
    public string Email { get; set; } = string.Empty;

    /// <summary>
    /// Gets or sets the customer's phone number.
    /// </summary>
    public string Phone { get; set; } = string.Empty;

    /// <summary>
    /// Gets or sets the street address.
    /// </summary>
    public string Address { get; set; } = string.Empty;

    /// <summary>
    /// Gets or sets the city name.
    /// </summary>
    public string City { get; set; } = string.Empty;

    /// <summary>
    /// Gets or sets the state or province abbreviation.
    /// </summary>
    public string State { get; set; } = string.Empty;

    /// <summary>
    /// Gets or sets the postal / ZIP code.
    /// </summary>
    public string ZipCode { get; set; } = string.Empty;

    /// <summary>
    /// Gets or sets the UTC timestamp indicating when the customer record was created.
    /// </summary>
    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
}
