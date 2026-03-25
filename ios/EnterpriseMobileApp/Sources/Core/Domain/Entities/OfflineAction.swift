import Foundation

/// Queued action to sync when connectivity returns.
public struct OfflineAction: Equatable, Sendable, Identifiable {
    public let id: String
    public let actionType: OfflineActionType
    public let payload: [String: String]
    public let createdAt: Date
    public var synced: Bool

    public init(
        id: String = UUID().uuidString,
        actionType: OfflineActionType,
        payload: [String: String],
        createdAt: Date = Date(),
        synced: Bool = false
    ) {
        self.id = id
        self.actionType = actionType
        self.payload = payload
        self.createdAt = createdAt
        self.synced = synced
    }
}

public enum OfflineActionType: String, Equatable, Sendable {
    case addToCart
    case removeFromCart
    case redeemReward
    case sendMessage
}
