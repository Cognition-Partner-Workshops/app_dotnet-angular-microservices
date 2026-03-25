import Foundation

// MARK: - Server-Driven UI Domain Entities

/// Polymorphic UI component model for server-driven rendering.
public enum UIComponent: Equatable, Sendable, Identifiable {
    case heroCard(HeroConfig)
    case actionPillRow(ActionPillConfig)
    case carousel(CarouselConfig)

    public var id: String {
        switch self {
        case .heroCard(let config): return "hero-\(config.id)"
        case .actionPillRow(let config): return "pills-\(config.id)"
        case .carousel(let config): return "carousel-\(config.id)"
        }
    }
}

/// Configuration for the full-bleed hero card.
public struct HeroConfig: Equatable, Sendable {
    public let id: String
    public let title: String
    public let subtitle: String
    public let imageURL: URL?
    public let gradientColors: [String]
    public let pricing: PricingInfo
    public let ctaText: String
    public let ctaDeepLink: String

    public init(
        id: String,
        title: String,
        subtitle: String,
        imageURL: URL?,
        gradientColors: [String],
        pricing: PricingInfo,
        ctaText: String,
        ctaDeepLink: String
    ) {
        self.id = id
        self.title = title
        self.subtitle = subtitle
        self.imageURL = imageURL
        self.gradientColors = gradientColors
        self.pricing = pricing
        self.ctaText = ctaText
        self.ctaDeepLink = ctaDeepLink
    }
}

/// Pricing information for product cards.
public struct PricingInfo: Equatable, Sendable {
    public let amount: Decimal
    public let currency: String
    public let period: String
    public let originalAmount: Decimal?

    public init(amount: Decimal, currency: String, period: String, originalAmount: Decimal? = nil) {
        self.amount = amount
        self.currency = currency
        self.period = period
        self.originalAmount = originalAmount
    }
}

/// Configuration for action pill row (Bill, Usage, etc.).
public struct ActionPillConfig: Equatable, Sendable {
    public let id: String
    public let pills: [ActionPill]

    public init(id: String, pills: [ActionPill]) {
        self.id = id
        self.pills = pills
    }
}

public struct ActionPill: Equatable, Sendable {
    public let label: String
    public let iconName: String
    public let deepLink: String

    public init(label: String, iconName: String, deepLink: String) {
        self.label = label
        self.iconName = iconName
        self.deepLink = deepLink
    }
}

/// Configuration for horizontal carousel (Just For You bundles).
public struct CarouselConfig: Equatable, Sendable {
    public let id: String
    public let title: String
    public let items: [CarouselItem]

    public init(id: String, title: String, items: [CarouselItem]) {
        self.id = id
        self.title = title
        self.items = items
    }
}

public struct CarouselItem: Equatable, Sendable, Identifiable {
    public let id: String
    public let title: String
    public let imageURL: URL?
    public let price: PricingInfo?
    public let deepLink: String

    public init(id: String, title: String, imageURL: URL?, price: PricingInfo?, deepLink: String) {
        self.id = id
        self.title = title
        self.imageURL = imageURL
        self.price = price
        self.deepLink = deepLink
    }
}
