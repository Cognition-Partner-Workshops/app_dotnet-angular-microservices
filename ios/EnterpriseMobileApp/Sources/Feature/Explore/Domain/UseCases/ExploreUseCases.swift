import Foundation
import CoreDomain

/// Use case for fetching explore feed with feature flag support for A/B testing.
public struct GetExploreFeedUseCase: Sendable {
    private let feedRepository: FeedRepository
    private let featureFlagUseCase: FeatureFlagUseCase

    public init(feedRepository: FeedRepository, featureFlagUseCase: FeatureFlagUseCase) {
        self.feedRepository = feedRepository
        self.featureFlagUseCase = featureFlagUseCase
    }

    public func execute() -> AsyncStream<DomainResult<[UIComponent]>> {
        let variant = featureFlagUseCase.variant(for: "explore_feed_layout")
        // Variant determines which feed layout to request
        _ = variant
        return feedRepository.getDynamicFeed()
    }
}
