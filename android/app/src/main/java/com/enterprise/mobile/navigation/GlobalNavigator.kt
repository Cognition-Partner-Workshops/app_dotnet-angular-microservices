package com.enterprise.mobile.navigation

import com.enterprise.core.domain.entities.AppRoute
import com.enterprise.core.domain.usecases.DeepLinkParser
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Global navigator that translates AppRoute intents to NavController actions.
 * Acts as the central nervous system of the app's navigation.
 */
@Singleton
class GlobalNavigator @Inject constructor(
    private val deepLinkParser: DeepLinkParser
) {
    /** Parses a URI and returns the resolved AppRoute. */
    fun resolveRoute(uri: String): AppRoute {
        return deepLinkParser.parse(uri)
    }

    /** Returns the nav destination route string for a given AppRoute. */
    fun destinationFor(route: AppRoute): String = when (route) {
        is AppRoute.Explore -> "explore"
        is AppRoute.Shop -> "shop"
        is AppRoute.Account -> "account"
        is AppRoute.Rewards -> "rewards"
        is AppRoute.AIConnect -> "aiconnect"
        is AppRoute.ProductDetail -> "shop/product/${route.productId}"
        is AppRoute.CartView -> "shop/cart"
        is AppRoute.CartAdd -> "shop/cart/add?itemId=${route.itemId}"
        is AppRoute.RewardDetail -> "rewards/${route.rewardId}"
        is AppRoute.ChatSession -> "aiconnect/session/${route.sessionId}"
        is AppRoute.Search -> "explore/search?q=${route.query}"
        is AppRoute.Notification -> "notifications/${route.notificationId}"
        is AppRoute.Unknown -> "explore"
    }
}
