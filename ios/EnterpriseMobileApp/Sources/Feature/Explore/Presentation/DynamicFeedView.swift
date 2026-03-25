import SwiftUI
import ComposableArchitecture
import CoreDomain
import CoreUIComponents

/// CMS-driven Explore screen with:
/// - Top Nav Bar (welcome greeting + icons)
/// - Floating AI search bar
/// - Hero banner carousel (~40% of screen)
/// - Service icons row (Bill, Usage, Manage Lines, Change Plan)
/// - Content sections: 5 carousels + 1 grid ("Everything You Need")
/// - Legacy polymorphic feed components
public struct DynamicFeedView: View {
    @Bindable var store: StoreOf<ExploreReducer>

    public init(store: StoreOf<ExploreReducer>) {
        self.store = store
    }

    public var body: some View {
        VStack(spacing: 0) {
            // ── CMS-driven Top Nav Bar ──────────────────────────────
            if let topNav = store.topNavBar {
                TopNavBarView(config: topNav) { deepLink in
                    store.send(.ctaTapped(deepLink))
                }
            }

            // ── Main scrollable content ─────────────────────────────
            ScrollView {
                LazyVStack(spacing: 0) {
                    // Floating AI Search Bar
                    FloatingSearchBarView(
                        query: $store.searchQuery.sending(\.searchQueryChanged),
                        placeholder: "Search Verizon"
                    )

                    // Hero Banner Carousel (~40% screen)
                    if !store.heroBanners.isEmpty {
                        HeroBannerCarouselView(banners: store.heroBanners) { deepLink in
                            store.send(.bannerTapped(deepLink))
                        }
                    }

                    // Service Icons Row
                    if !store.serviceIcons.isEmpty {
                        ServiceIconsRowView(icons: store.serviceIcons) { deepLink in
                            store.send(.serviceIconTapped(deepLink))
                        }
                    }

                    // CMS Content Sections (Carousels + Grid)
                    ForEach(store.exploreSections) { section in
                        ExploreSectionView(section: section) { deepLink in
                            store.send(.sectionItemTapped(deepLink))
                        }
                        .padding(.vertical, 12)
                    }

                    // Legacy Polymorphic Feed Components
                    ForEach(store.components) { component in
                        componentView(for: component)
                    }
                }
            }
            .refreshable { store.send(.refreshFeed) }
            .overlay {
                if store.isLoading && store.components.isEmpty && store.heroBanners.isEmpty {
                    ProgressView("Loading feed...")
                }
            }
        }
        .onAppear { store.send(.onAppear) }
    }

    @ViewBuilder
    private func componentView(for component: UIComponent) -> some View {
        switch component {
        case .heroCard(let config):
            HeroCardView(config: config) { deepLink in
                store.send(.ctaTapped(deepLink))
            }
            .padding(.horizontal, 16)

        case .actionPillRow(let config):
            ActionPillRowView(config: config) { deepLink in
                store.send(.pillTapped(deepLink))
            }

        case .carousel(let config):
            CarouselView(config: config) { deepLink in
                store.send(.carouselItemTapped(deepLink))
            }
        }
    }
}
