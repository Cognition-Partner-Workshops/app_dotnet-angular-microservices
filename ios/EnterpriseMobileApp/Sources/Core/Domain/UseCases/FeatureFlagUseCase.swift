import Foundation

/// Use case for determining which A/B test variant to render.
public struct FeatureFlagUseCase: Sendable {
    private let flags: [FeatureFlag]

    public init(flags: [FeatureFlag]) {
        self.flags = flags
    }

    public func isEnabled(_ key: String) -> Bool {
        flags.first(where: { $0.key == key })?.isEnabled ?? false
    }

    public func variant(for key: String) -> String {
        flags.first(where: { $0.key == key })?.variant ?? "control"
    }

    public func payload(for key: String) -> [String: String] {
        flags.first(where: { $0.key == key })?.payload ?? [:]
    }
}
