import SwiftUI
import ComposableArchitecture
import CoreDomain
import CoreUIComponents

/// Dynamic CMS-driven feed view using Switch over UIComponent enum.
public struct DynamicFeedView: View {
    let store: StoreOf<ExploreReducer>

    public init(store: StoreOf<ExploreReducer>) {
        self.store = store
    }

    public var body: some View {
        ScrollView {
            LazyVStack(spacing: 16) {
                ForEach(store.components) { component in
                    componentView(for: component)
                }
            }
            .padding(.vertical, 8)
        }
        .refreshable { store.send(.refreshFeed) }
        .overlay {
            if store.isLoading && store.components.isEmpty {
                ProgressView("Loading feed...")
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
