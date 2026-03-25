import Foundation
import CoreDomain

/// Firebase Analytics implementation - SDK stays in Data/Infrastructure layer only.
public final class FirebaseAnalyticsTracker: AnalyticsTracker, @unchecked Sendable {
    public init() {}

    public func trackScreen(_ name: String) {
        // In production: Analytics.logEvent(AnalyticsEventScreenView, parameters: [...])
        print("[Analytics] Screen: \(name)")
    }

    public func trackEvent(_ name: String, properties: [String: Any]) {
        // In production: Analytics.logEvent(name, parameters: properties)
        print("[Analytics] Event: \(name), props: \(properties)")
    }
}
