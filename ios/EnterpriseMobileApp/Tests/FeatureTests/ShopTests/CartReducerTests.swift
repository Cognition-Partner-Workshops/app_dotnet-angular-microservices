import XCTest
import ComposableArchitecture
@testable import CoreDomain
@testable import FeatureShop

final class CartReducerTests: XCTestCase {
    func testAddToCartUpdatesCount() async {
        let item = CartItem(id: "item-1", productId: "prod-1", name: "Fios Gigabit", price: 49.99)
        let fakeRepo = FakeCartRepository()

        let store = TestStore(initialState: CartReducer.State()) {
            CartReducer()
        } withDependencies: {
            $0.cartRepository = fakeRepo
        }

        await store.send(.addToCart(item)) {
            $0.isLoading = true
        }
        await store.receive(.operationResult(.success(()))) {
            $0.isLoading = false
        }
    }

    func testRemoveFromCart() async {
        var initialState = CartReducer.State()
        initialState.items = [
            CartItem(id: "item-1", productId: "prod-1", name: "Fios", price: 49.99)
        ]
        initialState.cartItemCount = 1

        let store = TestStore(initialState: initialState) {
            CartReducer()
        } withDependencies: {
            $0.cartRepository = FakeCartRepository()
        }

        await store.send(.removeFromCart("item-1"))
        await store.receive(.operationResult(.success(()))) {
            $0.isLoading = false
        }
    }
}

// MARK: - Fake

private final class FakeCartRepository: CartRepository, @unchecked Sendable {
    private var items: [CartItem] = []

    func getCartItems() -> AsyncStream<[CartItem]> {
        AsyncStream { continuation in
            continuation.yield(self.items)
            continuation.finish()
        }
    }

    func addItem(_ item: CartItem) async -> DomainResult<Void> {
        items.append(item)
        return .success(())
    }

    func removeItem(id: String) async -> DomainResult<Void> {
        items.removeAll { $0.id == id }
        return .success(())
    }

    func updateQuantity(itemId: String, quantity: Int) async -> DomainResult<Void> {
        if let index = items.firstIndex(where: { $0.id == itemId }) {
            items[index].quantity = quantity
        }
        return .success(())
    }

    func clearCart() async -> DomainResult<Void> {
        items.removeAll()
        return .success(())
    }

    func getCartItemCount() async -> Int {
        items.reduce(0) { $0 + $1.quantity }
    }
}
