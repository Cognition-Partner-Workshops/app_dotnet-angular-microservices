package com.enterprise.core.domain.interfaces

/** Analytics tracking interface - implemented in Infrastructure layer. No SDK bleed. */
interface AnalyticsTracker {
    fun trackScreen(name: String)
    fun trackEvent(name: String, properties: Map<String, Any> = emptyMap())
}
