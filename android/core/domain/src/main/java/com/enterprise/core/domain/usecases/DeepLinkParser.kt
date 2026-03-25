package com.enterprise.core.domain.usecases

import android.net.Uri
import com.enterprise.core.domain.entities.AppRoute
import javax.inject.Inject

/** Parses URI strings into strongly-typed AppRoute navigation intents. */
class DeepLinkParser @Inject constructor() {

    fun parse(uriString: String): AppRoute {
        if (uriString.isBlank()) return AppRoute.Unknown(uriString)

        val uri = try {
            Uri.parse(uriString)
        } catch (_: Exception) {
            return AppRoute.Unknown(uriString)
        }

        val pathSegments = uri.pathSegments ?: emptyList()
        val host = uri.host ?: return AppRoute.Unknown(uriString)

        return when (host) {
            "explore" -> AppRoute.Explore
            "shop" -> parseShopRoute(pathSegments, uri)
            "account" -> AppRoute.Account
            "rewards" -> parseRewardsRoute(pathSegments)
            "chat" -> parseChatRoute(pathSegments)
            "search" -> AppRoute.Search(uri.getQueryParameter("q"))
            "notification" -> {
                val id = pathSegments.firstOrNull() ?: return AppRoute.Unknown(uriString)
                AppRoute.Notification(id)
            }
            else -> AppRoute.Unknown(uriString)
        }
    }

    private fun parseShopRoute(segments: List<String>, uri: Uri): AppRoute {
        if (segments.isEmpty()) return AppRoute.Shop

        return when (segments[0]) {
            "cart" -> {
                if (segments.size > 1 && segments[1] == "add") {
                    val itemId = uri.getQueryParameter("itemId") ?: return AppRoute.CartView
                    AppRoute.CartAdd(itemId)
                } else {
                    AppRoute.CartView
                }
            }
            else -> AppRoute.ProductDetail(segments[0])
        }
    }

    private fun parseRewardsRoute(segments: List<String>): AppRoute {
        return if (segments.isEmpty()) {
            AppRoute.Rewards
        } else {
            AppRoute.RewardDetail(segments[0])
        }
    }

    private fun parseChatRoute(segments: List<String>): AppRoute {
        return AppRoute.ChatSession(segments.firstOrNull())
    }
}
