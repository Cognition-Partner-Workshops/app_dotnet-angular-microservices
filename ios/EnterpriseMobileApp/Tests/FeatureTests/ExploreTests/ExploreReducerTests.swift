import XCTest
import ComposableArchitecture
@testable import CoreDomain
@testable import FeatureExplore

final class ExploreReducerTests: XCTestCase {
    func testFeedLoadsOnAppear() async {
        let mockComponents: [UIComponent] = [
            .heroCard(HeroConfig(
                id: "hero-1",
                title: "Fios Gigabit",
                subtitle: "Blazing fast internet",
                imageURL: nil,
                gradientColors: ["#000000"],
                pricing: PricingInfo(amount: 49.99, currency: "$", period: "mo"),
                ctaText: "Shop Now",
                ctaDeepLink: "app://shop/fios"
            )),
            .actionPillRow(ActionPillConfig(
                id: "pills-1",
                pills: [ActionPill(label: "Bill", iconName: "doc.text", deepLink: "app://account/bill")]
            ))
        ]

        let store = TestStore(initialState: ExploreReducer.State()) {
            ExploreReducer()
        } withDependencies: {
            $0.feedRepository = FakeFeedRepository(feedResult: .success(mockComponents))
            $0.deepLinkRouter = FakeDeepLinkRouter()
        }

        await store.send(.onAppear) {
            $0.isLoading = true
        }
        await store.receive(.feedLoaded(.success(mockComponents))) {
            $0.isLoading = false
            $0.components = mockComponents
        }
    }

    func testEmptyFeedShowsError() async {
        let store = TestStore(initialState: ExploreReducer.State()) {
            ExploreReducer()
        } withDependencies: {
            $0.feedRepository = FakeFeedRepository(feedResult: .failure(.emptyData))
            $0.deepLinkRouter = FakeDeepLinkRouter()
        }

        await store.send(.onAppear) {
            $0.isLoading = true
        }
        await store.receive(.feedLoaded(.failure(.emptyData))) {
            $0.isLoading = false
            $0.error = .emptyData
        }
    }
}

// MARK: - Fakes

private struct FakeFeedRepository: FeedRepository {
    let feedResult: DomainResult<[UIComponent]>

    func getDynamicFeed() -> AsyncStream<DomainResult<[UIComponent]>> {
        AsyncStream { continuation in
            continuation.yield(feedResult)
            continuation.finish()
        }
    }
    func refreshFeed() async -> DomainResult<[UIComponent]> { feedResult }
    func getCachedFeed() async -> [UIComponent] { [] }
}

private struct FakeDeepLinkRouter: DeepLinkRouterProtocol {
    func navigate(to deepLink: String) async {}
}
