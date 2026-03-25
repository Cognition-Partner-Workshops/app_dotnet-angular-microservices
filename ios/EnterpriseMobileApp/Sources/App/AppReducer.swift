import Foundation
import ComposableArchitecture
import CoreDomain

/// Root app reducer managing tab navigation and deep link routing.
@Reducer
public struct AppReducer {
    public enum Tab: Equatable, Sendable {
        case explore
        case shop
        case account
        case rewards
        case aiConnect
    }

    @ObservableState
    public struct State: Equatable {
        public var selectedTab: Tab = .explore
        public var cartBadgeCount: Int = 0
        public var notificationCount: Int = 0

        public init() {}
    }

    public enum Action: Equatable, Sendable {
        case tabSelected(Tab)
        case cartBadgeUpdated(Int)
        case notificationCountUpdated(Int)
        case deepLinkReceived(String)
        case routeResolved(AppRoute)
    }

    public init() {}

    public var body: some ReducerOf<Self> {
        Reduce { state, action in
            switch action {
            case .tabSelected(let tab):
                state.selectedTab = tab
                return .none

            case .cartBadgeUpdated(let count):
                state.cartBadgeCount = count
                return .none

            case .notificationCountUpdated(let count):
                state.notificationCount = count
                return .none

            case .deepLinkReceived(let urlString):
                let parser = DeepLinkParser()
                let route = parser.parse(urlString)
                return .send(.routeResolved(route))

            case .routeResolved(let route):
                switch route {
                case .explore:
                    state.selectedTab = .explore
                case .shop, .productDetail, .cartView, .cartAdd:
                    state.selectedTab = .shop
                case .account:
                    state.selectedTab = .account
                case .rewards, .rewardDetail:
                    state.selectedTab = .rewards
                case .aiConnect, .chatSession:
                    state.selectedTab = .aiConnect
                case .search:
                    state.selectedTab = .explore
                case .notification:
                    break
                case .unknown:
                    break
                }
                return .none
            }
        }
    }
}
