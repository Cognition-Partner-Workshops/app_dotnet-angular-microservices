import Foundation
import CoreDomain

/// Authentication state for the Auth feature module.
public enum AuthState: Equatable, Sendable {
    case idle
    case authenticating
    case authenticated(User)
    case failed(DomainError)
}

/// Credentials for email/password login.
public struct LoginCredentials: Equatable, Sendable {
    public let email: String
    public let password: String

    public init(email: String, password: String) {
        self.email = email
        self.password = password
    }
}

/// Biometric authentication type.
public enum BiometricType: Equatable, Sendable {
    case faceID
    case touchID
    case none
}
