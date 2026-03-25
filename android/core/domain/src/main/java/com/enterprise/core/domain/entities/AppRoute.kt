package com.enterprise.core.domain.entities

/** Centralized deep link routing intent parsed from URIs. */
sealed class AppRoute {
    // Tab-level navigation
    data object Explore : AppRoute()
    data object Shop : AppRoute()
    data object Account : AppRoute()
    data object Rewards : AppRoute()
    data object AIConnect : AppRoute()

    // Feature-specific routes
    data class ProductDetail(val productId: String) : AppRoute()
    data class CartAdd(val itemId: String) : AppRoute()
    data object CartView : AppRoute()
    data class RewardDetail(val rewardId: String) : AppRoute()
    data class ChatSession(val sessionId: String?) : AppRoute()
    data class Search(val query: String?) : AppRoute()
    data class Notification(val notificationId: String) : AppRoute()
    data class Unknown(val path: String) : AppRoute()
}
