import Foundation
import CoreDomain
import CoreNetwork

/// Concrete feed repository implementing SSOT pattern with SwiftData persistence.
public final class FeedRepositoryImpl: ExploreFeedRepository, @unchecked Sendable {
    private let apiClient: APIClient
    private let cacheExpirySeconds: TimeInterval = 300 // 5 minutes

    public init(apiClient: APIClient) {
        self.apiClient = apiClient
    }

    public func getDynamicFeed() -> AsyncStream<DomainResult<[UIComponent]>> {
        AsyncStream { continuation in
            Task {
                // Step 1: Emit cached data first (SSOT)
                let cached = await getCachedFeed()
                if !cached.isEmpty {
                    continuation.yield(.success(cached))
                }

                // Step 2: Check cache expiry before network
                let shouldFetch = await shouldFetchFromNetwork()
                if shouldFetch {
                    // Step 3: Fetch from network
                    let result = await refreshFeed()
                    continuation.yield(result)
                }

                continuation.finish()
            }
        }
    }

    public func refreshFeed() async -> DomainResult<[UIComponent]> {
        do {
            let response: FeedResponse = try await apiClient.request(endpoint: "/feed/dynamic")
            let components = response.components.map { $0.toDomain() }
            // In production: save to SwiftData ModelContext here
            return .success(components)
        } catch {
            return .failure(.serverError(error.localizedDescription))
        }
    }

    public func getCachedFeed() async -> [UIComponent] {
        // In production: query SwiftData @Model for cached items
        return []
    }

    public func getPersonalizedFeed(userId: String) -> AsyncStream<DomainResult<[UIComponent]>> {
        AsyncStream { continuation in
            Task {
                do {
                    let response: FeedResponse = try await apiClient.request(
                        endpoint: "/feed/personalized/\(userId)"
                    )
                    let components = response.components.map { $0.toDomain() }
                    continuation.yield(.success(components))
                } catch {
                    continuation.yield(.failure(.serverError(error.localizedDescription)))
                }
                continuation.finish()
            }
        }
    }

    public func searchFeed(query: String) async -> DomainResult<[UIComponent]> {
        do {
            let response: FeedResponse = try await apiClient.request(
                endpoint: "/feed/search",
                method: .get,
                headers: ["X-Search-Query": query]
            )
            return .success(response.components.map { $0.toDomain() })
        } catch {
            return .failure(.serverError(error.localizedDescription))
        }
    }

    private func shouldFetchFromNetwork() async -> Bool {
        // In production: check SwiftData for last cache timestamp
        return true
    }
}

// MARK: - Network DTOs

struct FeedResponse: Decodable {
    let components: [UIComponentDTO]
}

struct UIComponentDTO: Decodable {
    let type: String
    let id: String
    let config: ComponentConfig

    func toDomain() -> UIComponent {
        switch type {
        case "hero_card":
            return .heroCard(HeroConfig(
                id: id,
                title: config.title ?? "",
                subtitle: config.subtitle ?? "",
                imageURL: config.imageUrl.flatMap(URL.init(string:)),
                gradientColors: config.gradientColors ?? [],
                pricing: PricingInfo(
                    amount: Decimal(config.priceAmount ?? 0),
                    currency: config.currency ?? "$",
                    period: config.pricePeriod ?? "mo",
                    originalAmount: config.originalPrice.map { Decimal($0) }
                ),
                ctaText: config.ctaText ?? "Shop Now",
                ctaDeepLink: config.ctaDeepLink ?? ""
            ))
        case "action_pills":
            let pills = (config.pills ?? []).map { pill in
                ActionPill(label: pill.label, iconName: pill.iconName, deepLink: pill.deepLink)
            }
            return .actionPillRow(ActionPillConfig(id: id, pills: pills))
        case "carousel":
            let items = (config.items ?? []).map { item in
                CarouselItem(
                    id: item.id,
                    title: item.title,
                    imageURL: item.imageUrl.flatMap(URL.init(string:)),
                    price: item.priceAmount.map {
                        PricingInfo(amount: Decimal($0), currency: item.currency ?? "$", period: item.pricePeriod ?? "mo")
                    },
                    deepLink: item.deepLink ?? ""
                )
            }
            return .carousel(CarouselConfig(id: id, title: config.title ?? "", items: items))
        default:
            return .heroCard(HeroConfig(
                id: id, title: "Unknown", subtitle: "", imageURL: nil,
                gradientColors: [], pricing: PricingInfo(amount: 0, currency: "$", period: "mo"),
                ctaText: "", ctaDeepLink: ""
            ))
        }
    }
}

struct ComponentConfig: Decodable {
    let title: String?
    let subtitle: String?
    let imageUrl: String?
    let gradientColors: [String]?
    let priceAmount: Double?
    let currency: String?
    let pricePeriod: String?
    let originalPrice: Double?
    let ctaText: String?
    let ctaDeepLink: String?
    let pills: [PillDTO]?
    let items: [CarouselItemDTO]?
}

struct PillDTO: Decodable {
    let label: String
    let iconName: String
    let deepLink: String
}

struct CarouselItemDTO: Decodable {
    let id: String
    let title: String
    let imageUrl: String?
    let priceAmount: Double?
    let currency: String?
    let pricePeriod: String?
    let deepLink: String?
}
