import Foundation

/// Use case for the AI Agent to log intent resolution decisions.
public struct LoggerUseCase: Sendable {
    private let analyticsTracker: AnalyticsTracker
    private let crashReporter: CrashReporter

    public init(analyticsTracker: AnalyticsTracker, crashReporter: CrashReporter) {
        self.analyticsTracker = analyticsTracker
        self.crashReporter = crashReporter
    }

    public func logLocalResolution(intent: String, confidence: Double) {
        analyticsTracker.trackEvent("ai_intent_resolved", properties: [
            "intent": intent,
            "source": "local_llm",
            "confidence": confidence
        ])
        crashReporter.logBreadcrumb("AI resolved '\(intent)' locally (confidence: \(confidence))")
    }

    public func logCloudFallback(intent: String, reason: String) {
        analyticsTracker.trackEvent("ai_intent_fallback", properties: [
            "intent": intent,
            "source": "cloud_llm",
            "reason": reason
        ])
        crashReporter.logBreadcrumb("AI fell back to cloud for '\(intent)': \(reason)")
    }
}
