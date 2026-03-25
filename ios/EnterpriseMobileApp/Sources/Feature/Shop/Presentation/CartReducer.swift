import Foundation
import ComposableArchitecture
import CoreDomain

/// TCA Reducer for Shopping Cart - global state with badge tracking.
@Reducer
public struct CartReducer {
    @ObservableState
    public struct State: Equatable {
        public var items: [CartItem] = []
        public var cartItemCount: Int = 0
        public var isLoading: Bool = false
        public var error: DomainError?

        public init() {}

        public var totalPrice: Decimal {
            items.reduce(Decimal.zero) { $0 + $1.totalPrice }
        }
    }

    public enum Action: Equatable, Sendable {
        case onAppear
        case cartUpdated([CartItem])
        case addToCart(CartItem)
        case removeFromCart(String)
        case updateQuantity(String, Int)
        case clearCart
        case operationResult(DomainResult<Void>)
    }

    @Dependency(\.cartRepository) var cartRepository

    public init() {}

    public var body: some ReducerOf<Self> {
        Reduce { state, action in
            switch action {
            case .onAppear:
                return .run { send in
                    let stream = cartRepository.getCartItems()
                    for await items in stream {
                        await send(.cartUpdated(items))
                    }
                }

            case .cartUpdated(let items):
                state.items = items
                state.cartItemCount = items.reduce(0) { $0 + $1.quantity }
                return .none

            case .addToCart(let item):
                state.isLoading = true
                return .run { send in
                    let result = await cartRepository.addItem(item)
                    await send(.operationResult(result))
                }

            case .removeFromCart(let itemId):
                return .run { send in
                    let result = await cartRepository.removeItem(id: itemId)
                    await send(.operationResult(result))
                }

            case .updateQuantity(let itemId, let quantity):
                return .run { send in
                    let result = await cartRepository.updateQuantity(itemId: itemId, quantity: quantity)
                    await send(.operationResult(result))
                }

            case .clearCart:
                return .run { send in
                    let result = await cartRepository.clearCart()
                    await send(.operationResult(result))
                }

            case .operationResult(let result):
                state.isLoading = false
                if case .failure(let error) = result {
                    state.error = error
                } else {
                    state.error = nil
                }
                return .none
            }
        }
    }
}

// MARK: - Dependencies

private enum CartRepositoryKey: DependencyKey {
    static let liveValue: any CartRepository = UnimplementedCartRepository()
}

extension DependencyValues {
    var cartRepository: any CartRepository {
        get { self[CartRepositoryKey.self] }
        set { self[CartRepositoryKey.self] = newValue }
    }
}

private struct UnimplementedCartRepository: CartRepository {
    func getCartItems() -> AsyncStream<[CartItem]> { AsyncStream { $0.finish() } }
    func addItem(_ item: CartItem) async -> DomainResult<Void> { .failure(.unknown("unimplemented")) }
    func removeItem(id: String) async -> DomainResult<Void> { .failure(.unknown("unimplemented")) }
    func updateQuantity(itemId: String, quantity: Int) async -> DomainResult<Void> { .failure(.unknown("unimplemented")) }
    func clearCart() async -> DomainResult<Void> { .failure(.unknown("unimplemented")) }
    func getCartItemCount() async -> Int { 0 }
}
