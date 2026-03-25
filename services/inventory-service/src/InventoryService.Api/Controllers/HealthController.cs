using Microsoft.AspNetCore.Mvc;

namespace InventoryService.Api.Controllers;

/// <summary>
/// Simple health-check endpoint used by Kubernetes liveness/readiness probes.
/// </summary>
[ApiController]
[Route("[controller]")]
public class HealthController : ControllerBase
{
    /// <summary>Returns service health status with a UTC timestamp.</summary>
    /// <response code="200">Service is healthy.</response>
    [HttpGet("/health")]
    [ProducesResponseType(StatusCodes.Status200OK)]
    public IActionResult Get() => Ok(new { status = "Healthy", service = "inventory-service", timestamp = DateTime.UtcNow });
}
