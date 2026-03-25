import Foundation

/// CMS repository contract for fetching server-driven UI configurations.
public protocol CMSRepository: Sendable {
    func getTopNavBar() async throws -> TopNavBarConfig
    func getBottomNavBar() async throws -> BottomNavBarConfig
    func getHeroBanners() async throws -> HeroBannerPayload
    func getServiceIcons() async throws -> ServiceIconsPayload
    func getExploreSections() async throws -> ExploreSectionsPayload
}
