import Foundation
import CoreDomain

/// Mock CMS repository that loads JSON from the bundle's MockData folder.
/// Used when MockForDemo flag is enabled.
public final class MockCMSRepositoryImpl: CMSRepository, @unchecked Sendable {
    private let decoder: JSONDecoder = {
        let d = JSONDecoder()
        return d
    }()

    public init() {}

    private func loadJSON<T: Decodable>(_ filename: String) throws -> T {
        // Try bundle resource first, then fall back to relative path
        if let url = Bundle.main.url(forResource: filename, withExtension: "json") {
            let data = try Data(contentsOf: url)
            return try decoder.decode(T.self, from: data)
        }

        // Fallback: hardcoded mock data
        let json = mockJSONData(for: filename)
        let data = json.data(using: .utf8)!
        return try decoder.decode(T.self, from: data)
    }

    public func getTopNavBar() async throws -> TopNavBarConfig {
        try loadJSON("top_nav_bar")
    }

    public func getBottomNavBar() async throws -> BottomNavBarConfig {
        try loadJSON("bottom_nav_bar")
    }

    public func getHeroBanners() async throws -> HeroBannerPayload {
        try loadJSON("hero_banners")
    }

    public func getServiceIcons() async throws -> ServiceIconsPayload {
        try loadJSON("service_icons")
    }

    public func getExploreSections() async throws -> ExploreSectionsPayload {
        try loadJSON("explore_sections")
    }

    // MARK: - Embedded Fallback Mock Data

    private func mockJSONData(for filename: String) -> String {
        switch filename {
        case "top_nav_bar":
            return """
            {
              "welcome_greeting": { "first_name": "John", "template": "Hi, {first_name}" },
              "icons": [
                { "id": "rewards", "type": "rewards", "label": "Rewards", "icon_name": "star", "badge_count": 3, "deep_link": "app://rewards" },
                { "id": "search", "type": "search", "label": "Search", "icon_name": "search", "badge_count": 0, "deep_link": "app://search" },
                { "id": "notification", "type": "notification", "label": "Notifications", "icon_name": "notifications", "badge_count": 5, "deep_link": "app://notifications" },
                { "id": "chat", "type": "chat", "label": "Chat", "icon_name": "chat", "badge_count": 2, "deep_link": "app://aiconnect" },
                { "id": "profile", "type": "profile", "label": "Profile", "icon_name": "person", "badge_count": 0, "deep_link": "app://account" }
              ]
            }
            """
        case "bottom_nav_bar":
            return """
            {
              "tabs": [
                { "id": "explore", "label": "Explore", "icon_name": "explore", "route": "explore", "badge_count": 0 },
                { "id": "shop", "label": "Shop", "icon_name": "shopping_bag", "route": "shop", "badge_count": 0 },
                { "id": "account", "label": "My Account", "icon_name": "person", "route": "account", "badge_count": 0 },
                { "id": "rewards", "label": "Rewards", "icon_name": "star", "route": "rewards", "badge_count": 0 },
                { "id": "aiconnect", "label": "AI Connect", "icon_name": "chat_bubble", "route": "aiconnect", "badge_count": 0 }
              ]
            }
            """
        case "hero_banners":
            return """
            {
              "banners": [
                { "id": "banner-1", "image_url": "https://images.unsplash.com/photo-1544197150-b99a580bb7a8?w=800&h=400&fit=crop", "image_target_section": "Explore, Top", "image_text": "Fios Gigabit Connection", "cta_text": "Shop Now", "cta_url": "app://shop/product/fios-gigabit" },
                { "id": "banner-2", "image_url": "https://images.unsplash.com/photo-1512941937669-90a1b58e7e9c?w=800&h=400&fit=crop", "image_target_section": "Explore, Top", "image_text": "iPhone 16 Pro", "cta_text": "Get Yours", "cta_url": "app://shop/product/iphone-16-pro" },
                { "id": "banner-3", "image_url": "https://images.unsplash.com/photo-1558618666-fcd25c85f82e?w=800&h=400&fit=crop", "image_target_section": "Explore, Top", "image_text": "Unlimited Plans", "cta_text": "See Plans", "cta_url": "app://shop/plans" },
                { "id": "banner-4", "image_url": "https://images.unsplash.com/photo-1551650975-87deedd944c3?w=800&h=400&fit=crop", "image_target_section": "Explore, Top", "image_text": "Stream with Verizon +play", "cta_text": "Learn More", "cta_url": "app://explore/vplay" }
              ]
            }
            """
        case "service_icons":
            return """
            {
              "icons": [
                { "id": "svc-bill", "icon_url": "https://img.icons8.com/fluency/96/bill.png", "label": "Bill", "deep_link": "app://account/bill" },
                { "id": "svc-usage", "icon_url": "https://img.icons8.com/fluency/96/combo-chart.png", "label": "Usage", "deep_link": "app://account/usage" },
                { "id": "svc-lines", "icon_url": "https://img.icons8.com/fluency/96/smartphone-tablet.png", "label": "Manage Lines", "deep_link": "app://account/lines" },
                { "id": "svc-plan", "icon_url": "https://img.icons8.com/fluency/96/change.png", "label": "Change Plan", "deep_link": "app://account/plan" }
              ]
            }
            """
        case "explore_sections":
            return """
            {
              "sections": [
                {
                  "id": "section-verizon-exclusives", "title": "Verizon Exclusives", "section_type": "carousel",
                  "items": [
                    { "id": "ve-1", "image_url": "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=300", "image_text": "Galaxy S24 Ultra", "context": "Save $200 with trade-in", "cta_text": "Add to Cart", "cta_action": "add_to_cart", "cta_deep_link": "app://shop/cart/add?itemId=galaxy-s24-ultra" },
                    { "id": "ve-2", "image_url": "https://images.unsplash.com/photo-1546868871-af0de0ae72be?w=300", "image_text": "Pixel 9 Pro", "context": "Get $100 gift card", "cta_text": "Add to Cart", "cta_action": "add_to_cart", "cta_deep_link": "app://shop/cart/add?itemId=pixel-9-pro" },
                    { "id": "ve-3", "image_url": "https://images.unsplash.com/photo-1585060544812-6b45742d762f?w=300", "image_text": "iPhone 16 Pro Max", "context": "5G Ultra Wideband included", "cta_text": "Add to Cart", "cta_action": "add_to_cart", "cta_deep_link": "app://shop/cart/add?itemId=iphone-16-pro-max" },
                    { "id": "ve-4", "image_url": "https://images.unsplash.com/photo-1434494878577-86c23bcb06b9?w=300", "image_text": "Apple Watch Ultra 2", "context": "Pair for $10/mo", "cta_text": "Add to Cart", "cta_action": "add_to_cart", "cta_deep_link": "app://shop/cart/add?itemId=apple-watch-ultra-2" },
                    { "id": "ve-5", "image_url": "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=300", "image_text": "AirPods Pro 2", "context": "Free with phone purchase", "cta_text": "Add to Cart", "cta_action": "add_to_cart", "cta_deep_link": "app://shop/cart/add?itemId=airpods-pro-2" }
                  ]
                },
                {
                  "id": "section-just-for-you", "title": "Just for You", "section_type": "carousel",
                  "items": [
                    { "id": "jfy-1", "image_url": "https://images.unsplash.com/photo-1558618666-fcd25c85f82e?w=300", "image_text": "Unlimited Ultimate", "context": "Saves you $15/mo", "cta_text": "Add to Cart", "cta_action": "add_to_cart", "cta_deep_link": "app://shop/cart/add?itemId=unlimited-ultimate" },
                    { "id": "jfy-2", "image_url": "https://images.unsplash.com/photo-1593642632559-0c6d3fc62b89?w=300", "image_text": "Disney+ Bundle", "context": "Based on streaming activity", "cta_text": "Add to Cart", "cta_action": "add_to_cart", "cta_deep_link": "app://shop/cart/add?itemId=disney-plus-bundle" },
                    { "id": "jfy-3", "image_url": "https://images.unsplash.com/photo-1544197150-b99a580bb7a8?w=300", "image_text": "Fios 2 Gig", "context": "Upgrade your Fios plan", "cta_text": "Add to Cart", "cta_action": "add_to_cart", "cta_deep_link": "app://shop/cart/add?itemId=fios-2-gig" },
                    { "id": "jfy-4", "image_url": "https://images.unsplash.com/photo-1563986768609-322da13575f2?w=300", "image_text": "Mobile Hotspot 50GB", "context": "Add-on for your plan", "cta_text": "Add to Cart", "cta_action": "add_to_cart", "cta_deep_link": "app://shop/cart/add?itemId=hotspot-50gb" },
                    { "id": "jfy-5", "image_url": "https://images.unsplash.com/photo-1551650975-87deedd944c3?w=300", "image_text": "Verizon +play Pass", "context": "Entertainment bundle", "cta_text": "Add to Cart", "cta_action": "add_to_cart", "cta_deep_link": "app://shop/cart/add?itemId=vplay-pass" }
                  ]
                },
                {
                  "id": "section-verizon-family", "title": "Verizon Family", "section_type": "carousel",
                  "items": [
                    { "id": "vf-1", "image_url": "https://images.unsplash.com/photo-1484723091739-30a097e8f929?w=300", "image_text": "Family Plan - 4 Lines", "context": "Save $40/mo", "cta_text": "Add to Cart", "cta_action": "add_to_cart", "cta_deep_link": "app://shop/cart/add?itemId=family-plan-4" },
                    { "id": "vf-2", "image_url": "https://images.unsplash.com/photo-1529156069898-49953e39b3ac?w=300", "image_text": "Kids Smartwatch", "context": "GizmoWatch for kids", "cta_text": "Add to Cart", "cta_action": "add_to_cart", "cta_deep_link": "app://shop/cart/add?itemId=gizmo-watch" },
                    { "id": "vf-3", "image_url": "https://images.unsplash.com/photo-1606107557195-0e29a4b5b4aa?w=300", "image_text": "Shared Data 30GB", "context": "One data pool for family", "cta_text": "Add to Cart", "cta_action": "add_to_cart", "cta_deep_link": "app://shop/cart/add?itemId=shared-data-30" },
                    { "id": "vf-4", "image_url": "https://images.unsplash.com/photo-1491013516836-7db643ee125a?w=300", "image_text": "Parental Controls+", "context": "Content filtering", "cta_text": "Add to Cart", "cta_action": "add_to_cart", "cta_deep_link": "app://shop/cart/add?itemId=parental-controls" },
                    { "id": "vf-5", "image_url": "https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=300", "image_text": "Home Internet Bundle", "context": "Mobile + Fios bundle", "cta_text": "Add to Cart", "cta_action": "add_to_cart", "cta_deep_link": "app://shop/cart/add?itemId=home-internet-bundle" }
                  ]
                },
                {
                  "id": "section-fios-tv-mobile", "title": "FIOS TV Mobile", "section_type": "carousel",
                  "items": [
                    { "id": "ftm-1", "image_url": "https://images.unsplash.com/photo-1593642632559-0c6d3fc62b89?w=300", "image_text": "Fios TV Your Way 350+", "context": "350+ channels", "cta_text": "Add to Cart", "cta_action": "add_to_cart", "cta_deep_link": "app://shop/cart/add?itemId=fios-tv-350" },
                    { "id": "ftm-2", "image_url": "https://images.unsplash.com/photo-1522869635100-9f4c5e86aa37?w=300", "image_text": "Fios TV Most Popular", "context": "125+ channels", "cta_text": "Add to Cart", "cta_action": "add_to_cart", "cta_deep_link": "app://shop/cart/add?itemId=fios-tv-most-popular" },
                    { "id": "ftm-3", "image_url": "https://images.unsplash.com/photo-1574375927938-d5a98e8d7e28?w=300", "image_text": "NFL Sunday Ticket", "context": "Every out-of-market game", "cta_text": "Add to Cart", "cta_action": "add_to_cart", "cta_deep_link": "app://shop/cart/add?itemId=nfl-sunday-ticket" },
                    { "id": "ftm-4", "image_url": "https://images.unsplash.com/photo-1560169897-fc0cdbdfa4d5?w=300", "image_text": "Stream TV Soundbar Pro", "context": "4K + premium sound", "cta_text": "Add to Cart", "cta_action": "add_to_cart", "cta_deep_link": "app://shop/cart/add?itemId=stream-tv-soundbar" },
                    { "id": "ftm-5", "image_url": "https://images.unsplash.com/photo-1593784991095-a205069470b6?w=300", "image_text": "Fios TV + Netflix", "context": "Save $5/mo on Netflix", "cta_text": "Add to Cart", "cta_action": "add_to_cart", "cta_deep_link": "app://shop/cart/add?itemId=fios-tv-netflix" }
                  ]
                },
                {
                  "id": "section-everything-you-need", "title": "Everything You Need", "section_type": "grid",
                  "items": [
                    { "id": "eyn-1", "image_url": "https://images.unsplash.com/photo-1544197150-b99a580bb7a8?w=300", "image_text": "5G Home Internet", "context": "Reliable 5G home internet", "cta_text": "Add to Cart", "cta_action": "add_to_cart", "cta_deep_link": "app://shop/cart/add?itemId=5g-home-internet" },
                    { "id": "eyn-2", "image_url": "https://images.unsplash.com/photo-1558618666-fcd25c85f82e?w=300", "image_text": "International Calling", "context": "230+ countries", "cta_text": "Add to Cart", "cta_action": "add_to_cart", "cta_deep_link": "app://shop/cart/add?itemId=intl-calling" },
                    { "id": "eyn-3", "image_url": "https://images.unsplash.com/photo-1551650975-87deedd944c3?w=300", "image_text": "Verizon Cloud 2TB", "context": "Back up everything", "cta_text": "Add to Cart", "cta_action": "add_to_cart", "cta_deep_link": "app://shop/cart/add?itemId=cloud-2tb" },
                    { "id": "eyn-4", "image_url": "https://images.unsplash.com/photo-1563986768609-322da13575f2?w=300", "image_text": "Device Protection", "context": "Cracked screen coverage", "cta_text": "Add to Cart", "cta_action": "add_to_cart", "cta_deep_link": "app://shop/cart/add?itemId=device-protection" },
                    { "id": "eyn-5", "image_url": "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=300", "image_text": "TravelPass", "context": "Use plan abroad $10/day", "cta_text": "Add to Cart", "cta_action": "add_to_cart", "cta_deep_link": "app://shop/cart/add?itemId=travelpass" },
                    { "id": "eyn-6", "image_url": "https://images.unsplash.com/photo-1434494878577-86c23bcb06b9?w=300", "image_text": "Number Share", "context": "Share with smartwatch", "cta_text": "Add to Cart", "cta_action": "add_to_cart", "cta_deep_link": "app://shop/cart/add?itemId=number-share" }
                  ]
                },
                {
                  "id": "section-verizon-store", "title": "Verizon Store", "section_type": "carousel",
                  "items": [
                    { "id": "vs-1", "image_url": "https://images.unsplash.com/photo-1585060544812-6b45742d762f?w=300", "image_text": "Galaxy Buds3 Pro", "context": "Premium ANC earbuds", "cta_text": "Add to Cart", "cta_action": "add_to_cart", "cta_deep_link": "app://shop/cart/add?itemId=galaxy-buds3-pro" },
                    { "id": "vs-2", "image_url": "https://images.unsplash.com/photo-1606107557195-0e29a4b5b4aa?w=300", "image_text": "OtterBox Defender", "context": "Military-grade protection", "cta_text": "Add to Cart", "cta_action": "add_to_cart", "cta_deep_link": "app://shop/cart/add?itemId=otterbox-defender" },
                    { "id": "vs-3", "image_url": "https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=300", "image_text": "Belkin Wireless Charger", "context": "15W fast charging", "cta_text": "Add to Cart", "cta_action": "add_to_cart", "cta_deep_link": "app://shop/cart/add?itemId=belkin-charger" },
                    { "id": "vs-4", "image_url": "https://images.unsplash.com/photo-1546868871-af0de0ae72be?w=300", "image_text": "JBL Flip 6 Speaker", "context": "Waterproof Bluetooth", "cta_text": "Add to Cart", "cta_action": "add_to_cart", "cta_deep_link": "app://shop/cart/add?itemId=jbl-flip-6" },
                    { "id": "vs-5", "image_url": "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=300", "image_text": "Anker Power Bank 20K", "context": "Charge phone 5x", "cta_text": "Add to Cart", "cta_action": "add_to_cart", "cta_deep_link": "app://shop/cart/add?itemId=anker-powerbank" }
                  ]
                }
              ]
            }
            """
        default:
            return "{}"
        }
    }
}
