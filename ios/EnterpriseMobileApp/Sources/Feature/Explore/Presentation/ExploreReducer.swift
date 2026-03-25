import Foundation
import ComposableArchitecture
import CoreDomain

/// TCA Reducer for Explore tab - drives the CMS-driven dynamic feed.
@Reducer
public struct ExploreReducer {
    @ObservableState
    public struct State: Equatable {
        // Legacy polymorphic feed components
        public var components: [UIComponent] = []
        // CMS-driven content
        public var topNavBar: TopNavBarConfig?
        public var bottomNavBar: BottomNavBarConfig?
        public var heroBanners: [HeroBanner] = []
        public var serviceIcons: [ServiceIcon] = []
        public var exploreSections: [ExploreSection] = []
        // UI state
        public var isLoading: Bool = false
        public var error: DomainError?
        public var searchQuery: String = ""
        public var isMockMode: Bool = MockForDemo.isEnabled

        public init() {}
    }

    public enum Action: Equatable, Sendable {
        case onAppear
        case feedLoaded(DomainResult<[UIComponent]>)
        case cmsContentLoaded(CMSContent)
        case cmsLoadFailed(String)
        case refreshFeed
        case searchQueryChanged(String)
        case ctaTapped(String)
        case pillTapped(String)
        case carouselItemTapped(String)
        case bannerTapped(String)
        case serviceIconTapped(String)
        case sectionItemTapped(String)
    }

    public struct CMSContent: Equatable, Sendable {
        public let topNavBar: TopNavBarConfig
        public let bottomNavBar: BottomNavBarConfig
        public let heroBanners: [HeroBanner]
        public let serviceIcons: [ServiceIcon]
        public let exploreSections: [ExploreSection]
    }

    @Dependency(\.feedRepository) var feedRepository
    @Dependency(\.deepLinkRouter) var deepLinkRouter
    @Dependency(\.cmsRepository) var cmsRepository

    public init() {}

    public var body: some ReducerOf<Self> {
        Reduce { state, action in
            switch action {
            case .onAppear:
                state.isLoading = true
                return .merge(
                    // Load legacy feed
                    .run { send in
                        let stream = feedRepository.getDynamicFeed()
                        for await result in stream {
                            await send(.feedLoaded(result))
                        }
                    },
                    // Load CMS content
                    .run { send in
                        do {
                            let topNav = try await cmsRepository.getTopNavBar()
                            let bottomNav = try await cmsRepository.getBottomNavBar()
                            let banners = try await cmsRepository.getHeroBanners()
                            let icons = try await cmsRepository.getServiceIcons()
                            let sections = try await cmsRepository.getExploreSections()
                            await send(.cmsContentLoaded(CMSContent(
                                topNavBar: topNav,
                                bottomNavBar: bottomNav,
                                heroBanners: banners.banners,
                                serviceIcons: icons.icons,
                                exploreSections: sections.sections
                            )))
                        } catch {
                            await send(.cmsLoadFailed(error.localizedDescription))
                        }
                    }
                )

            case .cmsContentLoaded(let content):
                state.topNavBar = content.topNavBar
                state.bottomNavBar = content.bottomNavBar
                state.heroBanners = content.heroBanners
                state.serviceIcons = content.serviceIcons
                state.exploreSections = content.exploreSections
                return .none

            case .cmsLoadFailed(let message):
                state.error = .unknown(message)
                return .none

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

            case .ctaTapped(let deepLink),
                 .pillTapped(let deepLink),
                 .carouselItemTapped(let deepLink),
                 .bannerTapped(let deepLink),
                 .serviceIconTapped(let deepLink),
                 .sectionItemTapped(let deepLink):
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

private enum CMSRepositoryKey: DependencyKey {
    static let liveValue: any CMSRepository = MockCMSRepositoryImpl()
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

    public var cmsRepository: any CMSRepository {
        get { self[CMSRepositoryKey.self] }
        set { self[CMSRepositoryKey.self] = newValue }
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
