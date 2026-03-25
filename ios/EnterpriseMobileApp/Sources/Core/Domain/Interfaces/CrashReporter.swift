import Foundation

/// Observability interface for crash reporting - no SDK bleed into Domain/Presentation.
public protocol CrashReporter: Sendable {
    func logNonFatal(_ error: Error)
    func setCustomKey(_ key: String, value: String)
    func logBreadcrumb(_ message: String)
}
