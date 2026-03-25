import Foundation
import CoreDomain

/// Tracks UX performance metrics like Time to Interactive (TTI).
public final class UXMetricsTracker: @unchecked Sendable {
    private let analyticsTracker: AnalyticsTracker
    private var startTimes: [String: CFAbsoluteTime] = [:]
    private let lock = NSLock()

    public init(analyticsTracker: AnalyticsTracker) {
        self.analyticsTracker = analyticsTracker
    }

    /// Call when a screen/feature starts loading.
    public func startMeasuring(_ label: String) {
        lock.lock()
        startTimes[label] = CFAbsoluteTimeGetCurrent()
        lock.unlock()
    }

    /// Call when the screen/feature becomes interactive. Logs the TTI metric.
    public func endMeasuring(_ label: String) {
        lock.lock()
        guard let start = startTimes.removeValue(forKey: label) else {
            lock.unlock()
            return
        }
        lock.unlock()

        let elapsed = CFAbsoluteTimeGetCurrent() - start
        let milliseconds = elapsed * 1000

        analyticsTracker.trackEvent("ux_time_to_interactive", properties: [
            "screen": label,
            "tti_ms": milliseconds
        ])

        // In production: use os_signpost for Instruments integration
        // os_signpost(.end, log: metricsLog, name: "TTI", "%{public}s: %.2fms", label, milliseconds)
    }
}
