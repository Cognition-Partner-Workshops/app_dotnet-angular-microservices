import SwiftUI
import ComposableArchitecture
import CoreDomain

/// Root tab navigation: Explore, Shop, Account, Rewards, AI Connect.
public struct MainTabView: View {
    @Bindable var store: StoreOf<AppReducer>

    public init(store: StoreOf<AppReducer>) {
        self.store = store
    }

    public var body: some View {
        TabView(selection: $store.selectedTab.sending(\.tabSelected)) {
            // Explore Tab
            NavigationStack {
                Text("Explore")
                    .navigationTitle("Explore")
                    .toolbar { topAppBarItems }
            }
            .tabItem {
                Label("Explore", systemImage: "globe")
            }
            .tag(AppReducer.Tab.explore)

            // Shop Tab
            NavigationStack {
                Text("Shop")
                    .navigationTitle("Shop")
                    .toolbar { topAppBarItems }
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

    @ToolbarContentBuilder
    private var topAppBarItems: some ToolbarContent {
        ToolbarItem(placement: .navigationBarLeading) {
            Button(action: {}) {
                Image(systemName: "magnifyingglass")
            }
            .accessibilityIdentifier("searchButton")
        }
        ToolbarItem(placement: .navigationBarTrailing) {
            HStack(spacing: 12) {
                Button(action: {}) {
                    Image(systemName: "bell")
                        .overlay(alignment: .topTrailing) {
                            if store.notificationCount > 0 {
                                Text("\(store.notificationCount)")
                                    .font(.caption2)
                                    .fontWeight(.bold)
                                    .foregroundColor(.white)
                                    .padding(4)
                                    .background(Color.red)
                                    .clipShape(Circle())
                                    .offset(x: 6, y: -6)
                            }
                        }
                }
                .accessibilityIdentifier("notificationBell")

                Button(action: {}) {
                    Image(systemName: "person.crop.circle")
                }
                .accessibilityIdentifier("profileButton")
            }
        }
    }
}
