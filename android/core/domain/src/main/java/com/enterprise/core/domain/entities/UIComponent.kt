package com.enterprise.core.domain.entities

import java.math.BigDecimal

// MARK: - Server-Driven UI Domain Entities

/** Polymorphic UI component model for server-driven rendering. */
sealed class UIComponent {
    abstract val id: String

    data class HeroCard(val config: HeroConfig) : UIComponent() {
        override val id: String get() = "hero-${config.id}"
    }

    data class ActionPillRow(val config: ActionPillConfig) : UIComponent() {
        override val id: String get() = "pills-${config.id}"
    }

    data class Carousel(val config: CarouselConfig) : UIComponent() {
        override val id: String get() = "carousel-${config.id}"
    }
}

/** Configuration for the full-bleed hero card. */
data class HeroConfig(
    val id: String,
    val title: String,
    val subtitle: String,
    val imageUrl: String?,
    val gradientColors: List<String>,
    val pricing: PricingInfo,
    val ctaText: String,
    val ctaDeepLink: String
)

/** Pricing information for product cards. */
data class PricingInfo(
    val amount: BigDecimal,
    val currency: String,
    val period: String,
    val originalAmount: BigDecimal? = null
)

/** Configuration for action pill row (Bill, Usage, etc.). */
data class ActionPillConfig(
    val id: String,
    val pills: List<ActionPill>
)

data class ActionPill(
    val label: String,
    val iconName: String,
    val deepLink: String
)

/** Configuration for horizontal carousel (Just For You bundles). */
data class CarouselConfig(
    val id: String,
    val title: String,
    val items: List<CarouselItem>
)

data class CarouselItem(
    val id: String,
    val title: String,
    val imageUrl: String?,
    val price: PricingInfo?,
    val deepLink: String
)
