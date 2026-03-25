import Foundation

/// Repository protocol for shopping cart operations.
public protocol CartRepository: Sendable {
    func getCartItems() -> AsyncStream<[CartItem]>
    func addItem(_ item: CartItem) async -> DomainResult<Void>
    func removeItem(id: String) async -> DomainResult<Void>
    func updateQuantity(itemId: String, quantity: Int) async -> DomainResult<Void>
    func clearCart() async -> DomainResult<Void>
    func getCartItemCount() async -> Int
}
