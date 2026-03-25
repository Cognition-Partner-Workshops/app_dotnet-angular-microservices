import Foundation

/// Centralized deep link routing intent parsed from URIs.
public enum AppRoute: Equatable, Sendable {
    // Tab-level navigation
    case explore
    case shop
    case account
    case rewards
    case aiConnect

    // Feature-specific routes
    case productDetail(productId: String)
    case cartAdd(itemId: String)
    case cartView
    case rewardDetail(rewardId: String)
    case chatSession(sessionId: String?)
    case search(query: String?)
    case notification(notificationId: String)
    case unknown(path: String)
}
