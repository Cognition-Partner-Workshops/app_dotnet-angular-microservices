import Foundation

/// Repository protocol for the dynamic feed (Server-Driven UI).
public protocol FeedRepository: Sendable {
    func getDynamicFeed() -> AsyncStream<DomainResult<[UIComponent]>>
    func refreshFeed() async -> DomainResult<[UIComponent]>
    func getCachedFeed() async -> [UIComponent]
}
