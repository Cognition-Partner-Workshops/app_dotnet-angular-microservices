package com.enterprise.feature.explore.data.dto

import com.enterprise.core.domain.entities.ActionPill
import com.enterprise.core.domain.entities.ActionPillConfig
import com.enterprise.core.domain.entities.CarouselConfig
import com.enterprise.core.domain.entities.CarouselItem
import com.enterprise.core.domain.entities.HeroConfig
import com.enterprise.core.domain.entities.PricingInfo
import com.enterprise.core.domain.entities.UIComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.math.BigDecimal

@Serializable
data class FeedResponse(
    val components: List<UIComponentDTO>
)

@Serializable
data class UIComponentDTO(
    val type: String,
    val id: String,
    val config: ComponentConfig
) {
    fun toDomain(): UIComponent = when (type) {
        "hero_card" -> UIComponent.HeroCard(
            HeroConfig(
                id = id,
                title = config.title ?: "",
                subtitle = config.subtitle ?: "",
                imageUrl = config.imageUrl,
                gradientColors = config.gradientColors ?: emptyList(),
                pricing = PricingInfo(
                    amount = BigDecimal(config.priceAmount ?: "0"),
                    currency = config.priceCurrency ?: "$",
                    period = config.pricePeriod ?: "mo"
                ),
                ctaText = config.ctaText ?: "Shop Now",
                ctaDeepLink = config.ctaDeepLink ?: ""
            )
        )
        "action_pill_row" -> UIComponent.ActionPillRow(
            ActionPillConfig(
                id = id,
                pills = config.pills?.map { pill ->
                    ActionPill(
                        label = pill.label,
                        iconName = pill.iconName,
                        deepLink = pill.deepLink
                    )
                } ?: emptyList()
            )
        )
        "carousel" -> UIComponent.Carousel(
            CarouselConfig(
                id = id,
                title = config.title ?: "",
                items = config.carouselItems?.map { item ->
                    CarouselItem(
                        id = item.id,
                        title = item.title,
                        imageUrl = item.imageUrl,
                        price = item.priceAmount?.let {
                            PricingInfo(
                                amount = BigDecimal(it),
                                currency = item.priceCurrency ?: "$",
                                period = item.pricePeriod ?: "mo"
                            )
                        },
                        deepLink = item.deepLink ?: ""
                    )
                } ?: emptyList()
            )
        )
        else -> UIComponent.HeroCard(
            HeroConfig(
                id = id, title = "Unknown", subtitle = "",
                imageUrl = null, gradientColors = emptyList(),
                pricing = PricingInfo(BigDecimal.ZERO, "$", "mo"),
                ctaText = "", ctaDeepLink = ""
            )
        )
    }
}

@Serializable
data class ComponentConfig(
    val title: String? = null,
    val subtitle: String? = null,
    @SerialName("image_url") val imageUrl: String? = null,
    @SerialName("gradient_colors") val gradientColors: List<String>? = null,
    @SerialName("price_amount") val priceAmount: String? = null,
    @SerialName("price_currency") val priceCurrency: String? = null,
    @SerialName("price_period") val pricePeriod: String? = null,
    @SerialName("cta_text") val ctaText: String? = null,
    @SerialName("cta_deep_link") val ctaDeepLink: String? = null,
    val pills: List<PillDTO>? = null,
    @SerialName("carousel_items") val carouselItems: List<CarouselItemDTO>? = null
)

@Serializable
data class PillDTO(
    val label: String,
    @SerialName("icon_name") val iconName: String,
    @SerialName("deep_link") val deepLink: String
)

@Serializable
data class CarouselItemDTO(
    val id: String,
    val title: String,
    @SerialName("image_url") val imageUrl: String? = null,
    @SerialName("price_amount") val priceAmount: String? = null,
    @SerialName("price_currency") val priceCurrency: String? = null,
    @SerialName("price_period") val pricePeriod: String? = null,
    @SerialName("deep_link") val deepLink: String? = null
)
