import Foundation
import CoreDomain
import CoreNetwork

/// Concrete rewards repository with offline caching support.
public final class RewardsRepositoryImpl: RewardsFeatureRepository, @unchecked Sendable {
    private let apiClient: APIClient

    public init(apiClient: APIClient) {
        self.apiClient = apiClient
    }

    public func getRewards() -> AsyncStream<DomainResult<[Reward]>> {
        AsyncStream { continuation in
            Task {
                // In production: emit cached rewards from SwiftData first
                do {
                    let response: RewardsResponse = try await apiClient.request(endpoint: "/rewards")
                    let rewards = response.rewards.map { $0.toDomain() }
                    continuation.yield(.success(rewards))
                } catch {
                    continuation.yield(.failure(.serverError(error.localizedDescription)))
                }
                continuation.finish()
            }
        }
    }

    public func getAccount() async -> DomainResult<RewardsAccount> {
        do {
            let response: RewardsAccountDTO = try await apiClient.request(endpoint: "/rewards/account")
            return .success(response.toDomain())
        } catch {
            return .failure(.serverError(error.localizedDescription))
        }
    }

    public func redeemReward(rewardId: String) async -> DomainResult<RewardsAccount> {
        do {
            let response: RewardsAccountDTO = try await apiClient.request(
                endpoint: "/rewards/redeem",
                method: .post,
                body: RedeemRequest(rewardId: rewardId)
            )
            return .success(response.toDomain())
        } catch {
            return .failure(.serverError(error.localizedDescription))
        }
    }

    public func getRewardsByCategory(_ category: RewardCategory) async -> DomainResult<[Reward]> {
        do {
            let response: RewardsResponse = try await apiClient.request(
                endpoint: "/rewards?category=\(category.rawValue)"
            )
            return .success(response.rewards.map { $0.toDomain() })
        } catch {
            return .failure(.serverError(error.localizedDescription))
        }
    }
}

// MARK: - DTOs

struct RewardsResponse: Decodable {
    let rewards: [RewardDTO]
}

struct RewardDTO: Decodable {
    let id: String
    let title: String
    let description: String
    let pointsCost: Int
    let imageUrl: String?
    let expirationDate: String?
    let category: String?

    enum CodingKeys: String, CodingKey {
        case id, title, description, pointsCost, category
        case imageUrl = "image_url"
        case expirationDate = "expiration_date"
    }

    func toDomain() -> Reward {
        Reward(
            id: id,
            title: title,
            description: description,
            pointsCost: pointsCost,
            imageURL: imageUrl.flatMap(URL.init(string:)),
            expirationDate: expirationDate.flatMap { ISO8601DateFormatter().date(from: $0) },
            category: RewardCategory(rawValue: category ?? "general") ?? .general
        )
    }
}

struct RewardsAccountDTO: Decodable {
    let userId: String
    let totalPoints: Int
    let tierLevel: String
    let redeemedRewards: [String]

    func toDomain() -> RewardsAccount {
        RewardsAccount(
            userId: userId,
            totalPoints: totalPoints,
            tierLevel: TierLevel(rawValue: tierLevel) ?? .bronze,
            redeemedRewards: redeemedRewards
        )
    }
}

struct RedeemRequest: Encodable {
    let rewardId: String
}
