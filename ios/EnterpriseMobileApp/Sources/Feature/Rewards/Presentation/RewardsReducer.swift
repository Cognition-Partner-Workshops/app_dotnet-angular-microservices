import Foundation
import ComposableArchitecture
import CoreDomain

/// TCA Reducer for Rewards feature.
@Reducer
public struct RewardsReducer {
    @ObservableState
    public struct State: Equatable {
        public var rewards: [Reward] = []
        public var account: RewardsAccount?
        public var isLoading: Bool = false
        public var error: DomainError?
        public var selectedReward: Reward?

        public init() {}
    }

    public enum Action: Equatable, Sendable {
        case onAppear
        case rewardsLoaded(DomainResult<[Reward]>)
        case accountLoaded(DomainResult<RewardsAccount>)
        case rewardSelected(Reward?)
        case redeemTapped(String)
        case redeemResult(DomainResult<RewardsAccount>)
    }

    @Dependency(\.rewardsRepository) var rewardsRepository

    public init() {}

    public var body: some ReducerOf<Self> {
        Reduce { state, action in
            switch action {
            case .onAppear:
                state.isLoading = true
                return .merge(
                    .run { send in
                        let stream = rewardsRepository.getRewards()
                        for await result in stream {
                            await send(.rewardsLoaded(result))
                        }
                    },
                    .run { send in
                        let result = await rewardsRepository.getAccount()
                        await send(.accountLoaded(result))
                    }
                )

            case .rewardsLoaded(let result):
                state.isLoading = false
                switch result {
                case .success(let rewards):
                    state.rewards = rewards
                case .failure(let error):
                    state.error = error
                }
                return .none

            case .accountLoaded(let result):
                if case .success(let account) = result {
                    state.account = account
                }
                return .none

            case .rewardSelected(let reward):
                state.selectedReward = reward
                return .none

            case .redeemTapped(let rewardId):
                state.isLoading = true
                return .run { send in
                    let result = await rewardsRepository.redeemReward(rewardId: rewardId)
                    await send(.redeemResult(result))
                }

            case .redeemResult(let result):
                state.isLoading = false
                switch result {
                case .success(let account):
                    state.account = account
                case .failure(let error):
                    state.error = error
                }
                return .none
            }
        }
    }
}

// MARK: - Dependencies

private enum RewardsRepositoryKey: DependencyKey {
    static let liveValue: any RewardsRepository = UnimplementedRewardsRepository()
}

extension DependencyValues {
    var rewardsRepository: any RewardsRepository {
        get { self[RewardsRepositoryKey.self] }
        set { self[RewardsRepositoryKey.self] = newValue }
    }
}

private struct UnimplementedRewardsRepository: RewardsRepository {
    func getRewards() -> AsyncStream<DomainResult<[Reward]>> { AsyncStream { $0.finish() } }
    func getAccount() async -> DomainResult<RewardsAccount> { .failure(.unknown("unimplemented")) }
    func redeemReward(rewardId: String) async -> DomainResult<RewardsAccount> { .failure(.unknown("unimplemented")) }
}
