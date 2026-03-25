import Foundation

/// Shopping cart item domain entity.
public struct CartItem: Equatable, Sendable, Identifiable {
    public let id: String
    public let productId: String
    public let name: String
    public let price: Decimal
    public var quantity: Int
    public let imageURL: URL?

    public init(
        id: String,
        productId: String,
        name: String,
        price: Decimal,
        quantity: Int = 1,
        imageURL: URL? = nil
    ) {
        self.id = id
        self.productId = productId
        self.name = name
        self.price = price
        self.quantity = quantity
        self.imageURL = imageURL
    }

    public var totalPrice: Decimal {
        price * Decimal(quantity)
    }
}
