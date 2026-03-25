import Foundation

/// Repository protocol for authentication operations.
public protocol AuthRepository: Sendable {
    func login(email: String, password: String) async -> DomainResult<User>
    func loginWithBiometric() async -> DomainResult<User>
    func loginWithPasskey(challenge: Data) async -> DomainResult<User>
    func logout() async -> DomainResult<Void>
    func getCurrentUser() async -> DomainResult<User>
    func refreshToken() async -> DomainResult<Void>
}
