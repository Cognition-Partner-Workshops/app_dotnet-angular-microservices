import Foundation
import CoreDomain

/// Domain entity for a cached feed item (used for SSOT/offline persistence).
public struct DomainFeedItem: Equatable, Sendable, Identifiable {
    public let id: String
    public let component: UIComponent
    public let sortOrder: Int
    public let lastUpdated: Date

    public init(id: String, component: UIComponent, sortOrder: Int, lastUpdated: Date = Date()) {
        self.id = id
        self.component = component
        self.sortOrder = sortOrder
        self.lastUpdated = lastUpdated
    }

    /// Check if cache is expired (5-minute TTL).
    public var isCacheExpired: Bool {
        Date().timeIntervalSince(lastUpdated) > 300
    }
}
