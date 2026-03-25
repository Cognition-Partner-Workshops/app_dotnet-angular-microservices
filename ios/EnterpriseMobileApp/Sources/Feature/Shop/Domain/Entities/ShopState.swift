import Foundation
import CoreDomain

/// Shopping feature state.
public struct ShopState: Equatable, Sendable {
    public var cartItems: [CartItem]
    public var cartItemCount: Int
    public var isProcessing: Bool
    public var error: DomainError?

    public init(
        cartItems: [CartItem] = [],
        cartItemCount: Int = 0,
        isProcessing: Bool = false,
        error: DomainError? = nil
    ) {
        self.cartItems = cartItems
        self.cartItemCount = cartItemCount
        self.isProcessing = isProcessing
        self.error = error
    }

    public var totalPrice: Decimal {
        cartItems.reduce(Decimal.zero) { $0 + $1.totalPrice }
    }
}
