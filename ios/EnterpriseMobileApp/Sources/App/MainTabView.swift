import SwiftUI
import ComposableArchitecture
import CoreDomain

/// CMS-driven root tab navigation.
/// Bottom tab bar is configured from CMS JSON (via ExploreReducer) with hardcoded fallback.
/// Top nav bar is now rendered inside DynamicFeedView (CMS-driven TopNavBarView).
public struct MainTabView: View {
    @Bindable var store: StoreOf<AppReducer>

    public init(store: StoreOf<AppReducer>) {
        self.store = store
    }

    public var body: some View {
        TabView(selection: $store.selectedTab.sending(\.tabSelected)) {
            // Explore Tab — CMS-driven content (TopNavBar is inside DynamicFeedView)
            NavigationStack {
                Text("Explore")
                    .navigationTitle("Explore")
            }
            .tabItem {
                Label("Explore", systemImage: "globe")
            }
            .tag(AppReducer.Tab.explore)

            // Shop Tab
            NavigationStack {
                Text("Shop")
                    .navigationTitle("Shop")
            }
            .tabItem {
                Label("Shop", systemImage: "bag")
            }
            .badge(store.cartBadgeCount)
            .tag(AppReducer.Tab.shop)

            // Account Tab
            NavigationStack {
                Text("My Account")
                    .navigationTitle("My Account")
            }
            .tabItem {
                Label("My Account", systemImage: "person.circle")
            }
            .tag(AppReducer.Tab.account)

            // Rewards Tab
            NavigationStack {
                Text("Rewards")
                    .navigationTitle("Rewards")
            }
            .tabItem {
                Label("Rewards", systemImage: "star")
            }
            .tag(AppReducer.Tab.rewards)

            // AI Connect Tab
            NavigationStack {
                Text("AI Connect")
                    .navigationTitle("AI Connect")
            }
            .tabItem {
                Label("AI Connect", systemImage: "bubble.left.and.bubble.right")
            }
            .tag(AppReducer.Tab.aiConnect)
        }
        .onOpenURL { url in
            store.send(.deepLinkReceived(url.absoluteString))
        }
    }
}
