import Foundation
import CoreDomain

/// Use case for removing items from the cart.
public struct RemoveFromCartUseCase: Sendable {
    private let cartRepository: CartRepository

    public init(cartRepository: CartRepository) {
        self.cartRepository = cartRepository
    }

    public func execute(itemId: String) async -> DomainResult<Void> {
        await cartRepository.removeItem(id: itemId)
    }
}

/// Use case for updating item quantity in the cart.
public struct UpdateCartQuantityUseCase: Sendable {
    private let cartRepository: CartRepository

    public init(cartRepository: CartRepository) {
        self.cartRepository = cartRepository
    }

    public func execute(itemId: String, quantity: Int) async -> DomainResult<Void> {
        if quantity <= 0 {
            return await cartRepository.removeItem(id: itemId)
        }
        return await cartRepository.updateQuantity(itemId: itemId, quantity: quantity)
    }
}
