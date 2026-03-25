import Foundation

/// Centralized deep link parser translating URIs into domain-level routing intents.
public struct DeepLinkParser: Sendable {
    public init() {}

    public func parse(_ urlString: String) -> AppRoute {
        guard let url = URL(string: urlString),
              let host = url.host else {
            return .unknown(path: urlString)
        }

        let pathComponents = url.pathComponents.filter { $0 != "/" }
        let queryItems = URLComponents(url: url, resolvingAgainstBaseURL: false)?
            .queryItems?.reduce(into: [String: String]()) { dict, item in
                dict[item.name] = item.value
            } ?? [:]

        switch host {
        case "explore":
            return .explore
        case "shop":
            if pathComponents.isEmpty { return .shop }
            switch pathComponents[0] {
            case "cart":
                if pathComponents.count > 1 && pathComponents[1] == "add",
                   let itemId = queryItems["itemId"] {
                    return .cartAdd(itemId: itemId)
                }
                return .cartView
            default:
                return .productDetail(productId: pathComponents[0])
            }
        case "account":
            return .account
        case "rewards":
            if let rewardId = pathComponents.first {
                return .rewardDetail(rewardId: rewardId)
            }
            return .rewards
        case "chat":
            let sessionId = pathComponents.first
            return .chatSession(sessionId: sessionId)
        case "search":
            return .search(query: queryItems["q"])
        case "notification":
            if let notifId = pathComponents.first {
                return .notification(notificationId: notifId)
            }
            return .unknown(path: urlString)
        default:
            return .unknown(path: urlString)
        }
    }
}
