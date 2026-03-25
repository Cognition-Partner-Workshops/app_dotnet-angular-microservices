import Foundation

/// Core User entity - pure domain model with no OS dependencies.
public struct User: Equatable, Identifiable, Sendable {
    public let id: String
    public let email: String
    public let displayName: String
    public let avatarURL: URL?
    public let isVerified: Bool

    public init(
        id: String,
        email: String,
        displayName: String,
        avatarURL: URL? = nil,
        isVerified: Bool = false
    ) {
        self.id = id
        self.email = email
        self.displayName = displayName
        self.avatarURL = avatarURL
        self.isVerified = isVerified
    }
}
