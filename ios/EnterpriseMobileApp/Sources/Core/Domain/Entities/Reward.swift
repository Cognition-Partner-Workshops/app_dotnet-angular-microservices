import Foundation

/// Rewards program domain entity.
public struct Reward: Equatable, Sendable, Identifiable {
    public let id: String
    public let title: String
    public let description: String
    public let pointsCost: Int
    public let imageURL: URL?
    public let expirationDate: Date?
    public let category: RewardCategory

    public init(
        id: String,
        title: String,
        description: String,
        pointsCost: Int,
        imageURL: URL? = nil,
        expirationDate: Date? = nil,
        category: RewardCategory = .general
    ) {
        self.id = id
        self.title = title
        self.description = description
        self.pointsCost = pointsCost
        self.imageURL = imageURL
        self.expirationDate = expirationDate
        self.category = category
    }
}

public enum RewardCategory: String, Equatable, Sendable {
    case general
    case streaming
    case dining
    case travel
    case merchandise
}

/// User's rewards account summary.
public struct RewardsAccount: Equatable, Sendable {
    public let userId: String
    public let totalPoints: Int
    public let tierLevel: TierLevel
    public let redeemedRewards: [String]

    public init(userId: String, totalPoints: Int, tierLevel: TierLevel, redeemedRewards: [String] = []) {
        self.userId = userId
        self.totalPoints = totalPoints
        self.tierLevel = tierLevel
        self.redeemedRewards = redeemedRewards
    }
}

public enum TierLevel: String, Equatable, Sendable {
    case bronze
    case silver
    case gold
    case platinum
}
