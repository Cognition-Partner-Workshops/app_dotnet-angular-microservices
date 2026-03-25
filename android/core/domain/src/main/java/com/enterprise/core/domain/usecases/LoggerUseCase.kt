package com.enterprise.core.domain.usecases

import com.enterprise.core.domain.interfaces.AnalyticsTracker
import javax.inject.Inject

/** AI agent logs intent resolution decisions (local vs. cloud LLM). */
class LoggerUseCase @Inject constructor(
    private val analyticsTracker: AnalyticsTracker
) {
    fun logLocalResolution(intent: String, confidence: Double) {
        analyticsTracker.trackEvent("agent_intent_resolved", mapOf(
            "intent" to intent,
            "source" to "local_llm",
            "confidence" to confidence.toString()
        ))
    }

    fun logCloudFallback(intent: String, reason: String) {
        analyticsTracker.trackEvent("agent_intent_resolved", mapOf(
            "intent" to intent,
            "source" to "cloud_llm",
            "fallback_reason" to reason
        ))
    }
}
