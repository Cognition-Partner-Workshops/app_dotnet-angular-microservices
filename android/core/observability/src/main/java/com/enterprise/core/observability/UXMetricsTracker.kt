package com.enterprise.core.observability

import com.enterprise.core.domain.interfaces.AnalyticsTracker
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Tracks UX rendering performance metrics (Time to Interactive).
 * Measures time from ViewModel init to first Success state emission.
 */
@Singleton
class UXMetricsTracker @Inject constructor(
    private val analyticsTracker: AnalyticsTracker
) {
    private val startTimes = mutableMapOf<String, Long>()

    /** Call when a screen/component starts loading. */
    fun markStart(tag: String) {
        startTimes[tag] = System.nanoTime()
    }

    /** Call when the first successful state is emitted. Logs TTI metric. */
    fun markInteractive(tag: String) {
        val startTime = startTimes.remove(tag) ?: return
        val elapsedMs = (System.nanoTime() - startTime) / 1_000_000
        analyticsTracker.trackEvent("ux_time_to_interactive", mapOf(
            "screen" to tag,
            "tti_ms" to elapsedMs
        ))
    }
}
