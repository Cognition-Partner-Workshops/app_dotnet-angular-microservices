import Foundation
import CoreDomain

/// Feature-specific repository for Rewards.
public protocol RewardsFeatureRepository: RewardsRepository {
    func getRewardsByCategory(_ category: RewardCategory) async -> DomainResult<[Reward]>
}
