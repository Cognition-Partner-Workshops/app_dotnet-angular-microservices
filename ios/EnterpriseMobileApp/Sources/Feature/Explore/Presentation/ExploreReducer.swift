import Foundation
import ComposableArchitecture
import CoreDomain

/// TCA Reducer for Explore tab - drives the dynamic CMS feed.
@Reducer
public struct ExploreReducer {
    @ObservableState
    public struct State: Equatable {
        public var components: [UIComponent] = []
        public var isLoading: Bool = false
        public var error: DomainError?
        public var searchQuery: String = ""

        public init() {}
    }

    public enum Action: Equatable, Sendable {
        case onAppear
        case feedLoaded(DomainResult<[UIComponent]>)
        case refreshFeed
        case searchQueryChanged(String)
        case ctaTapped(String)
        case pillTapped(String)
        case carouselItemTapped(String)
    }

    @Dependency(\.feedRepository) var feedRepository
    @Dependency(\.deepLinkRouter) var deepLinkRouter

    public init() {}

    public var body: some ReducerOf<Self> {
        Reduce { state, action in
            switch action {
            case .onAppear:
                state.isLoading = true
                return .run { send in
                    let stream = feedRepository.getDynamicFeed()
                    for await result in stream {
                        await send(.feedLoaded(result))
                    }
                }

            case .feedLoaded(let result):
                state.isLoading = false
                switch result {
                case .success(let components):
                    state.components = components
                    state.error = nil
                case .failure(let error):
                    state.error = error
                }
                return .none

            case .refreshFeed:
                state.isLoading = true
                return .run { send in
                    let result = await feedRepository.refreshFeed()
                    await send(.feedLoaded(result))
                }

            case .searchQueryChanged(let query):
                state.searchQuery = query
                return .none

            case .ctaTapped(let deepLink), .pillTapped(let deepLink), .carouselItemTapped(let deepLink):
                return .run { _ in
                    await deepLinkRouter.navigate(to: deepLink)
                }
            }
        }
    }
}

// MARK: - Dependencies

private enum FeedRepositoryKey: DependencyKey {
    static let liveValue: any FeedRepository = UnimplementedFeedRepository()
}

private enum DeepLinkRouterKey: DependencyKey {
    static let liveValue: DeepLinkRouterProtocol = UnimplementedDeepLinkRouter()
}

extension DependencyValues {
    var feedRepository: any FeedRepository {
        get { self[FeedRepositoryKey.self] }
        set { self[FeedRepositoryKey.self] = newValue }
    }

    var deepLinkRouter: DeepLinkRouterProtocol {
        get { self[DeepLinkRouterKey.self] }
        set { self[DeepLinkRouterKey.self] = newValue }
    }
}

public protocol DeepLinkRouterProtocol: Sendable {
    func navigate(to deepLink: String) async
}

private struct UnimplementedFeedRepository: FeedRepository {
    func getDynamicFeed() -> AsyncStream<DomainResult<[UIComponent]>> {
        AsyncStream { $0.finish() }
    }
    func refreshFeed() async -> DomainResult<[UIComponent]> { .failure(.unknown("unimplemented")) }
    func getCachedFeed() async -> [UIComponent] { [] }
}

private struct UnimplementedDeepLinkRouter: DeepLinkRouterProtocol {
    func navigate(to deepLink: String) async {}
}
