import Foundation
import CoreDomain

/// Use case for Passkey authentication via Apple's AuthenticationServices.
public struct PasskeyUseCase: Sendable {
    private let authRepository: AuthRepository

    public init(authRepository: AuthRepository) {
        self.authRepository = authRepository
    }

    public func execute(challenge: Data) async -> DomainResult<User> {
        await authRepository.loginWithPasskey(challenge: challenge)
    }
}
