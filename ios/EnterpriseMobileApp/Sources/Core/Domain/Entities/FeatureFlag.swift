import Foundation

/// Feature flag for A/B testing and gradual rollouts.
public struct FeatureFlag: Equatable, Sendable {
    public let key: String
    public let variant: String
    public let isEnabled: Bool
    public let payload: [String: String]

    public init(key: String, variant: String = "control", isEnabled: Bool = false, payload: [String: String] = [:]) {
        self.key = key
        self.variant = variant
        self.isEnabled = isEnabled
        self.payload = payload
    }
}
