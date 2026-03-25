package com.enterprise.core.observability

import com.enterprise.core.domain.interfaces.AnalyticsTracker
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Firebase Analytics implementation of AnalyticsTracker.
 * No SDK bleed: Firebase Analytics API stays here, never leaks to Domain/Presentation.
 */
@Singleton
class FirebaseAnalyticsTracker @Inject constructor() : AnalyticsTracker {

    override fun trackScreen(name: String) {
        // Firebase.analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) { param("screen_name", name) }
        android.util.Log.d(TAG, "Screen: $name")
    }

    override fun trackEvent(name: String, properties: Map<String, Any>) {
        // Firebase.analytics.logEvent(name) { properties.forEach { (k, v) -> param(k, v.toString()) } }
        android.util.Log.d(TAG, "Event: $name, props: $properties")
    }

    companion object {
        private const val TAG = "AnalyticsTracker"
    }
}
