package com.enterprise.feature.explore.domain.entities

import com.enterprise.core.domain.entities.UIComponent
import java.util.Date

/** Domain feed item with cache expiry tracking for SSOT pattern. */
data class DomainFeedItem(
    val id: String,
    val component: UIComponent,
    val sortOrder: Int,
    val lastUpdated: Date = Date()
) {
    /** Returns true if cached data is older than 5 minutes. */
    val isCacheExpired: Boolean
        get() {
            val fiveMinutesMs = 5 * 60 * 1000L
            return Date().time - lastUpdated.time > fiveMinutesMs
        }
}
