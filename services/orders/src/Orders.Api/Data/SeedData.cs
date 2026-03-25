using Orders.Api.Models;

namespace Orders.Api.Data;

public static class SeedData
{
    public static void Initialize(OrdersDbContext context)
    {
        context.Database.EnsureCreated();
        if (context.Orders.Any()) return;

        var order = new Order
        {
            CustomerId = 1,
            ShippingAddress = "123 Main St, Springfield, IL 62701",
            Status = "Completed",
            TotalAmount = 29.97m,
            Items = new List<OrderItem>
            {
                new OrderItem { ProductId = 1, Quantity = 3, UnitPrice = 9.99m }
            }
        };
        context.Orders.Add(order);
        context.SaveChanges();
    }
}
