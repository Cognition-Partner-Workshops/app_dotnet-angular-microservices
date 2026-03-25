import Foundation

/// Repository protocol for rewards operations.
public protocol RewardsRepository: Sendable {
    func getRewards() -> AsyncStream<DomainResult<[Reward]>>
    func getAccount() async -> DomainResult<RewardsAccount>
    func redeemReward(rewardId: String) async -> DomainResult<RewardsAccount>
}
