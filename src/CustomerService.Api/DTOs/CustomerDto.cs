namespace CustomerService.Api.DTOs;

/// <summary>
/// Data transfer object for cross-service communication.
/// Contains the fields that other services (e.g., Orders) need for operations like shipping address lookup.
/// </summary>
public class CustomerDto
{
    /// <summary>
    /// Gets or sets the customer identifier.
    /// </summary>
    public int Id { get; set; }

    /// <summary>
    /// Gets or sets the customer's full name or company name.
    /// </summary>
    public string Name { get; set; } = string.Empty;

    /// <summary>
    /// Gets or sets the customer's email address.
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
}
