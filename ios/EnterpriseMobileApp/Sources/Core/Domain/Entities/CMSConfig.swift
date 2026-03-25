import Foundation

// MARK: - MockForDemo Flag

/// Global flag to toggle between mock (local JSON) and live API data.
public enum MockForDemo {
    public static var isEnabled: Bool = true
}

// MARK: - Top Nav Bar

public struct TopNavBarConfig: Codable, Equatable, Sendable {
    public let welcomeGreeting: WelcomeGreeting
    public let icons: [NavIcon]

    enum CodingKeys: String, CodingKey {
        case welcomeGreeting = "welcome_greeting"
        case icons
    }
}

public struct WelcomeGreeting: Codable, Equatable, Sendable {
    public let firstName: String
    public let template: String

    enum CodingKeys: String, CodingKey {
        case firstName = "first_name"
        case template
    }

    public init(firstName: String, template: String = "Hi, {first_name}") {
        self.firstName = firstName
        self.template = template
    }
}

public struct NavIcon: Codable, Equatable, Identifiable, Sendable {
    public let id: String
    public let type: NavIconType
    public let label: String
    public let iconName: String
    public let badgeCount: Int
    public let deepLink: String?

    enum CodingKeys: String, CodingKey {
        case id, type, label
        case iconName = "icon_name"
        case badgeCount = "badge_count"
        case deepLink = "deep_link"
    }
}

public enum NavIconType: String, Codable, Equatable, Sendable {
    case rewards
    case search
    case notification
    case chat
    case profile
}

// MARK: - Bottom Nav Bar

public struct BottomNavBarConfig: Codable, Equatable, Sendable {
    public let tabs: [BottomNavTab]
}

public struct BottomNavTab: Codable, Equatable, Identifiable, Sendable {
    public let id: String
    public let label: String
    public let iconName: String
    public let route: String
    public let badgeCount: Int

    enum CodingKeys: String, CodingKey {
        case id, label, route
        case iconName = "icon_name"
        case badgeCount = "badge_count"
    }
}

// MARK: - Hero Banner Carousel

public struct HeroBannerPayload: Codable, Equatable, Sendable {
    public let banners: [HeroBanner]
}

public struct HeroBanner: Codable, Equatable, Identifiable, Sendable {
    public let id: String
    public let imageUrl: String
    public let imageTargetSection: String
    public let imageText: String
    public let ctaText: String
    public let ctaUrl: String

    enum CodingKeys: String, CodingKey {
        case id
        case imageUrl = "image_url"
        case imageTargetSection = "image_target_section"
        case imageText = "image_text"
        case ctaText = "cta_text"
        case ctaUrl = "cta_url"
    }
}

// MARK: - Service Icons

public struct ServiceIconsPayload: Codable, Equatable, Sendable {
    public let icons: [ServiceIcon]
}

public struct ServiceIcon: Codable, Equatable, Identifiable, Sendable {
    public let id: String
    public let iconUrl: String
    public let label: String
    public let deepLink: String

    enum CodingKeys: String, CodingKey {
        case id, label
        case iconUrl = "icon_url"
        case deepLink = "deep_link"
    }
}

// MARK: - Content Sections (Carousels + Grids)

public struct ExploreSectionsPayload: Codable, Equatable, Sendable {
    public let sections: [ExploreSection]
}

public struct ExploreSection: Codable, Equatable, Identifiable, Sendable {
    public let id: String
    public let title: String
    public let sectionType: SectionType
    public let items: [SectionItem]

    enum CodingKeys: String, CodingKey {
        case id, title, items
        case sectionType = "section_type"
    }
}

public enum SectionType: String, Codable, Equatable, Sendable {
    case carousel
    case grid
}

public struct SectionItem: Codable, Equatable, Identifiable, Sendable {
    public let id: String
    public let imageUrl: String
    public let imageText: String
    public let context: String
    public let ctaText: String
    public let ctaAction: String
    public let ctaDeepLink: String

    enum CodingKeys: String, CodingKey {
        case id, context
        case imageUrl = "image_url"
        case imageText = "image_text"
        case ctaText = "cta_text"
        case ctaAction = "cta_action"
        case ctaDeepLink = "cta_deep_link"
    }
}
