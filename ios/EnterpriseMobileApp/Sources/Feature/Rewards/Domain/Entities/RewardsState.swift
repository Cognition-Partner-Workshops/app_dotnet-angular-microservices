import Foundation
import CoreDomain

/// Rewards feature state.
public struct RewardsState: Equatable, Sendable {
    public var rewards: [Reward]
    public var account: RewardsAccount?
    public var isLoading: Bool
    public var error: DomainError?
    public var selectedReward: Reward?

    public init(
        rewards: [Reward] = [],
        account: RewardsAccount? = nil,
        isLoading: Bool = false,
        error: DomainError? = nil,
        selectedReward: Reward? = nil
    ) {
        self.rewards = rewards
        self.account = account
        self.isLoading = isLoading
        self.error = error
        self.selectedReward = selectedReward
    }
}
