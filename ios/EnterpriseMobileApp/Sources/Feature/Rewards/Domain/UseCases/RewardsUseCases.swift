import Foundation
import CoreDomain

/// Use case for fetching available rewards.
public struct GetRewardsUseCase: Sendable {
    private let rewardsRepository: RewardsRepository

    public init(rewardsRepository: RewardsRepository) {
        self.rewardsRepository = rewardsRepository
    }

    public func execute() -> AsyncStream<DomainResult<[Reward]>> {
        rewardsRepository.getRewards()
    }
}

/// Use case for redeeming a reward.
public struct RedeemRewardUseCase: Sendable {
    private let rewardsRepository: RewardsRepository

    public init(rewardsRepository: RewardsRepository) {
        self.rewardsRepository = rewardsRepository
    }

    public func execute(rewardId: String) async -> DomainResult<RewardsAccount> {
        await rewardsRepository.redeemReward(rewardId: rewardId)
    }
}
