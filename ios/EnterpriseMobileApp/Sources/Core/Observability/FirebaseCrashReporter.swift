import Foundation
import CoreDomain

/// Firebase Crashlytics implementation - SDK stays in Data/Infrastructure layer only.
public final class FirebaseCrashReporter: CrashReporter, @unchecked Sendable {
    public init() {}

    public func logNonFatal(_ error: Error) {
        // In production: Crashlytics.crashlytics().record(error: error)
        print("[CrashReporter] Non-fatal: \(error.localizedDescription)")
    }

    public func setCustomKey(_ key: String, value: String) {
        // In production: Crashlytics.crashlytics().setCustomValue(value, forKey: key)
        print("[CrashReporter] Key: \(key) = \(value)")
    }

    public func logBreadcrumb(_ message: String) {
        // In production: Crashlytics.crashlytics().log(message)
        print("[CrashReporter] Breadcrumb: \(message)")
    }
}
