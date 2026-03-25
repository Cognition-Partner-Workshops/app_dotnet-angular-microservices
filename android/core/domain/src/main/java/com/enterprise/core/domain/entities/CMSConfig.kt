package com.enterprise.core.domain.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** MockForDemo flag - when true, uses local mock JSON instead of live APIs. */
object MockForDemo {
    var isEnabled: Boolean = true
}

// ── Top Nav Bar ────────────────────────────────────────────────────────────

@Serializable
data class TopNavBarConfig(
    @SerialName("welcome_greeting") val welcomeGreeting: WelcomeGreeting,
    val icons: List<NavIcon>
)

@Serializable
data class WelcomeGreeting(
    @SerialName("first_name") val firstName: String,
    val template: String = "Hi, {first_name}"
)

@Serializable
data class NavIcon(
    val id: String,
    val type: NavIconType,
    val label: String,
    @SerialName("icon_name") val iconName: String,
    @SerialName("badge_count") val badgeCount: Int = 0,
    @SerialName("deep_link") val deepLink: String? = null
)

@Serializable
enum class NavIconType {
    @SerialName("rewards") REWARDS,
    @SerialName("search") SEARCH,
    @SerialName("notification") NOTIFICATION,
    @SerialName("chat") CHAT,
    @SerialName("profile") PROFILE
}

// ── Bottom Nav Bar ─────────────────────────────────────────────────────────

@Serializable
data class BottomNavBarConfig(
    val tabs: List<BottomNavTab>
)

@Serializable
data class BottomNavTab(
    val id: String,
    val label: String,
    @SerialName("icon_name") val iconName: String,
    val route: String,
    @SerialName("badge_count") val badgeCount: Int = 0
)

// ── Hero Banner Carousel ───────────────────────────────────────────────────

@Serializable
data class HeroBannerPayload(
    val banners: List<HeroBanner>
)

@Serializable
data class HeroBanner(
    val id: String,
    @SerialName("image_url") val imageUrl: String,
    @SerialName("image_target_section") val imageTargetSection: String,
    @SerialName("image_text") val imageText: String,
    @SerialName("cta_text") val ctaText: String,
    @SerialName("cta_url") val ctaUrl: String
)

// ── Service Icons ──────────────────────────────────────────────────────────

@Serializable
data class ServiceIconsPayload(
    val icons: List<ServiceIcon>
)

@Serializable
data class ServiceIcon(
    val id: String,
    @SerialName("icon_url") val iconUrl: String,
    val label: String,
    @SerialName("deep_link") val deepLink: String
)

// ── Content Sections (Carousels + Grids) ───────────────────────────────────

@Serializable
data class ExploreSectionsPayload(
    val sections: List<ExploreSection>
)

@Serializable
data class ExploreSection(
    val id: String,
    val title: String,
    @SerialName("section_type") val sectionType: SectionType,
    val items: List<SectionItem>
)

@Serializable
enum class SectionType {
    @SerialName("carousel") CAROUSEL,
    @SerialName("grid") GRID
}

@Serializable
data class SectionItem(
    val id: String,
    @SerialName("image_url") val imageUrl: String,
    @SerialName("image_text") val imageText: String,
    val context: String,
    @SerialName("cta_text") val ctaText: String,
    @SerialName("cta_action") val ctaAction: String,
    @SerialName("cta_deep_link") val ctaDeepLink: String
)
