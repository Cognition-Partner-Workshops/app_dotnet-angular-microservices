import Foundation
import CoreDomain

/// Feature-specific repository for Explore feed operations.
public protocol ExploreFeedRepository: FeedRepository {
    func getPersonalizedFeed(userId: String) -> AsyncStream<DomainResult<[UIComponent]>>
    func searchFeed(query: String) async -> DomainResult<[UIComponent]>
}
