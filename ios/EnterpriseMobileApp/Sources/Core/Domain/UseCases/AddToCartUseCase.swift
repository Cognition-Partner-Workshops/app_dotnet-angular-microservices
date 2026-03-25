import Foundation

/// Use case for adding items to the shopping cart (supports offline queueing).
public struct AddToCartUseCase: Sendable {
    private let cartRepository: CartRepository
    private let offlineActionRepository: OfflineActionRepository

    public init(cartRepository: CartRepository, offlineActionRepository: OfflineActionRepository) {
        self.cartRepository = cartRepository
        self.offlineActionRepository = offlineActionRepository
    }

    public func execute(item: CartItem) async -> DomainResult<Void> {
        let result = await cartRepository.addItem(item)
        switch result {
        case .success:
            return .success(())
        case .failure(let error):
            if case .networkUnavailable = error {
                let offlineAction = OfflineAction(
                    actionType: .addToCart,
                    payload: [
                        "productId": item.productId,
                        "name": item.name,
                        "price": "\(item.price)",
                        "quantity": "\(item.quantity)"
                    ]
                )
                return await offlineActionRepository.enqueue(offlineAction)
            }
            return .failure(error)
        }
    }
}
