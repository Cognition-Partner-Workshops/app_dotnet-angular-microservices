import XCTest
import ComposableArchitecture
@testable import CoreDomain
@testable import FeatureRewards

final class RewardsReducerTests: XCTestCase {
    func testRewardsLoadOnAppear() async {
        let mockRewards = [
            Reward(id: "r-1", title: "Free Movie", description: "Stream a free movie", pointsCost: 500),
            Reward(id: "r-2", title: "$5 Gift Card", description: "Dining credit", pointsCost: 1000, category: .dining)
        ]
        let mockAccount = RewardsAccount(userId: "u-1", totalPoints: 5000, tierLevel: .gold)

        let store = TestStore(initialState: RewardsReducer.State()) {
            RewardsReducer()
        } withDependencies: {
            $0.rewardsRepository = FakeRewardsRepository(
                rewards: mockRewards,
                account: mockAccount
            )
        }

        store.exhaustivity = .off

        await store.send(.onAppear) {
            $0.isLoading = true
        }

        await store.receive(.rewardsLoaded(.success(mockRewards))) {
            $0.isLoading = false
            $0.rewards = mockRewards
        }

        await store.receive(.accountLoaded(.success(mockAccount))) {
            $0.account = mockAccount
        }
    }

    func testRedeemReward() async {
        let updatedAccount = RewardsAccount(userId: "u-1", totalPoints: 4500, tierLevel: .gold, redeemedRewards: ["r-1"])

        let store = TestStore(initialState: RewardsReducer.State()) {
            RewardsReducer()
        } withDependencies: {
            $0.rewardsRepository = FakeRewardsRepository(
                rewards: [],
                account: updatedAccount
            )
        }

        await store.send(.redeemTapped("r-1")) {
            $0.isLoading = true
        }
        await store.receive(.redeemResult(.success(updatedAccount))) {
            $0.isLoading = false
            $0.account = updatedAccount
        }
    }
}

// MARK: - Fake

private struct FakeRewardsRepository: RewardsRepository {
    let rewards: [Reward]
    let account: RewardsAccount

    func getRewards() -> AsyncStream<DomainResult<[Reward]>> {
        AsyncStream { continuation in
            continuation.yield(.success(rewards))
            continuation.finish()
        }
    }

    func getAccount() async -> DomainResult<RewardsAccount> {
        .success(account)
    }

    func redeemReward(rewardId: String) async -> DomainResult<RewardsAccount> {
        .success(account)
    }
}
