import Foundation
import CoreDomain

/// Feature-specific repository for Shop operations.
public protocol ShopRepository: CartRepository {
    func getProducts() -> AsyncStream<DomainResult<[UIComponent]>>
    func getProductDetail(productId: String) async -> DomainResult<HeroConfig>
}
