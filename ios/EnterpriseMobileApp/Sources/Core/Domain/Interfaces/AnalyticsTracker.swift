import Foundation

/// Observability interface for analytics - no SDK bleed into Domain/Presentation.
public protocol AnalyticsTracker: Sendable {
    func trackScreen(_ name: String)
    func trackEvent(_ name: String, properties: [String: Any])
}
