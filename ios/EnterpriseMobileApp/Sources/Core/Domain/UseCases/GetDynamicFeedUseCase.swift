import Foundation

/// Use case for fetching the dynamic CMS-driven feed with SSOT pattern.
public struct GetDynamicFeedUseCase: Sendable {
    private let feedRepository: FeedRepository

    public init(feedRepository: FeedRepository) {
        self.feedRepository = feedRepository
    }

    /// Returns a reactive stream of feed components (SSOT: local DB -> network -> local DB -> auto-emit).
    public func execute() -> AsyncStream<DomainResult<[UIComponent]>> {
        feedRepository.getDynamicFeed()
    }

    /// Forces a network refresh of the feed.
    public func refresh() async -> DomainResult<[UIComponent]> {
        await feedRepository.refreshFeed()
    }
}
