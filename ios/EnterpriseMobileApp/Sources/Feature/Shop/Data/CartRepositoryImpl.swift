import Foundation
import CoreDomain
import CoreNetwork

/// Concrete cart repository with offline support.
public final class CartRepositoryImpl: ShopRepository, @unchecked Sendable {
    private let apiClient: APIClient
    private var items: [CartItem] = []
    private var continuations: [AsyncStream<[CartItem]>.Continuation] = []
    private let lock = NSLock()

    public init(apiClient: APIClient) {
        self.apiClient = apiClient
    }

    public func getCartItems() -> AsyncStream<[CartItem]> {
        AsyncStream { [weak self] continuation in
            guard let self else { return }
            self.lock.lock()
            self.continuations.append(continuation)
            continuation.yield(self.items)
            self.lock.unlock()

            continuation.onTermination = { @Sendable [weak self] _ in
                self?.removeContinuation(continuation)
            }
        }
    }

    public func addItem(_ item: CartItem) async -> DomainResult<Void> {
        lock.lock()
        if let index = items.firstIndex(where: { $0.productId == item.productId }) {
            items[index].quantity += item.quantity
        } else {
            items.append(item)
        }
        let currentItems = items
        lock.unlock()
        notifyAll(currentItems)
        return .success(())
    }

    public func removeItem(id: String) async -> DomainResult<Void> {
        lock.lock()
        items.removeAll { $0.id == id }
        let currentItems = items
        lock.unlock()
        notifyAll(currentItems)
        return .success(())
    }

    public func updateQuantity(itemId: String, quantity: Int) async -> DomainResult<Void> {
        lock.lock()
        if let index = items.firstIndex(where: { $0.id == itemId }) {
            items[index].quantity = quantity
        }
        let currentItems = items
        lock.unlock()
        notifyAll(currentItems)
        return .success(())
    }

    public func clearCart() async -> DomainResult<Void> {
        lock.lock()
        items.removeAll()
        lock.unlock()
        notifyAll([])
        return .success(())
    }

    public func getCartItemCount() async -> Int {
        lock.lock()
        let count = items.reduce(0) { $0 + $1.quantity }
        lock.unlock()
        return count
    }

    public func getProducts() -> AsyncStream<DomainResult<[UIComponent]>> {
        AsyncStream { continuation in
            Task {
                do {
                    let response: FeedResponse = try await apiClient.request(endpoint: "/shop/products")
                    let components = response.components.map { $0.toDomain() }
                    continuation.yield(.success(components))
                } catch {
                    continuation.yield(.failure(.serverError(error.localizedDescription)))
                }
                continuation.finish()
            }
        }
    }

    public func getProductDetail(productId: String) async -> DomainResult<HeroConfig> {
        do {
            let dto: UIComponentDTO = try await apiClient.request(endpoint: "/shop/products/\(productId)")
            if case .heroCard(let config) = dto.toDomain() {
                return .success(config)
            }
            return .failure(.notFound)
        } catch {
            return .failure(.serverError(error.localizedDescription))
        }
    }

    private func notifyAll(_ items: [CartItem]) {
        lock.lock()
        let conts = continuations
        lock.unlock()
        for continuation in conts {
            continuation.yield(items)
        }
    }

    private func removeContinuation(_ target: AsyncStream<[CartItem]>.Continuation) {
        lock.lock()
        continuations.removeAll { $0 as AnyObject === target as AnyObject }
        lock.unlock()
    }
}
