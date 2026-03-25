import Foundation

/// Use case for user login with multiple authentication methods.
public struct LoginUseCase: Sendable {
    private let authRepository: AuthRepository
    private let analyticsTracker: AnalyticsTracker

    public init(authRepository: AuthRepository, analyticsTracker: AnalyticsTracker) {
        self.authRepository = authRepository
        self.analyticsTracker = analyticsTracker
    }

    public func execute(email: String, password: String) async -> DomainResult<User> {
        let result = await authRepository.login(email: email, password: password)
        switch result {
        case .success:
            analyticsTracker.trackEvent("login_success", properties: ["method": "credentials"])
        case .failure(let error):
            analyticsTracker.trackEvent("login_failure", properties: ["method": "credentials", "error": "\(error)"])
        }
        return result
    }

    public func executeWithBiometric() async -> DomainResult<User> {
        let result = await authRepository.loginWithBiometric()
        switch result {
        case .success:
            analyticsTracker.trackEvent("login_success", properties: ["method": "biometric"])
        case .failure(let error):
            analyticsTracker.trackEvent("login_failure", properties: ["method": "biometric", "error": "\(error)"])
        }
        return result
    }

    public func executeWithPasskey(challenge: Data) async -> DomainResult<User> {
        let result = await authRepository.loginWithPasskey(challenge: challenge)
        switch result {
        case .success:
            analyticsTracker.trackEvent("login_success", properties: ["method": "passkey"])
        case .failure(let error):
            analyticsTracker.trackEvent("login_failure", properties: ["method": "passkey", "error": "\(error)"])
        }
        return result
    }
}
