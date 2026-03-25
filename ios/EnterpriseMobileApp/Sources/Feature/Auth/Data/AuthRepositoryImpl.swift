import Foundation
import CoreDomain
import CoreNetwork

/// Concrete implementation of AuthRepository using secure HTTP and Keychain.
public final class AuthRepositoryImpl: AuthFeatureRepository, @unchecked Sendable {
    private let apiClient: APIClient
    private let keychainService: KeychainService

    private static let tokenKey = "auth_access_token"
    private static let refreshTokenKey = "auth_refresh_token"

    public init(apiClient: APIClient, keychainService: KeychainService) {
        self.apiClient = apiClient
        self.keychainService = keychainService
    }

    public func login(email: String, password: String) async -> DomainResult<User> {
        do {
            let response: AuthResponse = try await apiClient.request(
                endpoint: "/auth/login",
                method: .post,
                body: LoginRequest(email: email, password: password)
            )
            storeTokens(access: response.accessToken, refresh: response.refreshToken)
            return .success(response.user.toDomain())
        } catch {
            return .failure(mapError(error))
        }
    }

    public func loginWithBiometric() async -> DomainResult<User> {
        guard let token = keychainService.load(key: Self.refreshTokenKey) else {
            return .failure(.unauthorized)
        }
        do {
            let response: AuthResponse = try await apiClient.request(
                endpoint: "/auth/refresh",
                method: .post,
                body: RefreshRequest(refreshToken: token)
            )
            storeTokens(access: response.accessToken, refresh: response.refreshToken)
            return .success(response.user.toDomain())
        } catch {
            return .failure(mapError(error))
        }
    }

    public func loginWithPasskey(challenge: Data) async -> DomainResult<User> {
        do {
            let response: AuthResponse = try await apiClient.request(
                endpoint: "/auth/passkey/verify",
                method: .post,
                body: PasskeyVerifyRequest(challenge: challenge.base64EncodedString())
            )
            storeTokens(access: response.accessToken, refresh: response.refreshToken)
            return .success(response.user.toDomain())
        } catch {
            return .failure(mapError(error))
        }
    }

    public func logout() async -> DomainResult<Void> {
        _ = keychainService.delete(key: Self.tokenKey)
        _ = keychainService.delete(key: Self.refreshTokenKey)
        return .success(())
    }

    public func getCurrentUser() async -> DomainResult<User> {
        do {
            let response: UserDTO = try await apiClient.request(endpoint: "/auth/me")
            return .success(response.toDomain())
        } catch {
            return .failure(mapError(error))
        }
    }

    public func refreshToken() async -> DomainResult<Void> {
        guard let token = keychainService.load(key: Self.refreshTokenKey) else {
            return .failure(.unauthorized)
        }
        do {
            let response: AuthResponse = try await apiClient.request(
                endpoint: "/auth/refresh",
                method: .post,
                body: RefreshRequest(refreshToken: token)
            )
            storeTokens(access: response.accessToken, refresh: response.refreshToken)
            return .success(())
        } catch {
            return .failure(mapError(error))
        }
    }

    public func checkBiometricAvailability() async -> BiometricType {
        // In production: use LAContext to check biometric type
        return .faceID
    }

    public func registerPasskey(userId: String) async -> DomainResult<Data> {
        do {
            let response: PasskeyChallengeResponse = try await apiClient.request(
                endpoint: "/auth/passkey/register",
                method: .post,
                body: PasskeyRegisterRequest(userId: userId)
            )
            guard let challengeData = Data(base64Encoded: response.challenge) else {
                return .failure(.unknown("Invalid challenge data"))
            }
            return .success(challengeData)
        } catch {
            return .failure(mapError(error))
        }
    }

    // MARK: - Private Helpers

    private func storeTokens(access: String, refresh: String) {
        _ = keychainService.save(key: Self.tokenKey, value: access)
        _ = keychainService.save(key: Self.refreshTokenKey, value: refresh)
    }

    private func mapError(_ error: Error) -> DomainError {
        if let apiError = error as? APIError {
            switch apiError {
            case .httpError(let code, _) where code == 401:
                return .unauthorized
            case .httpError(let code, _) where code == 404:
                return .notFound
            case .networkUnavailable:
                return .networkUnavailable
            default:
                return .serverError(error.localizedDescription)
            }
        }
        return .unknown(error.localizedDescription)
    }
}

// MARK: - DTOs

struct LoginRequest: Encodable {
    let email: String
    let password: String
}

struct RefreshRequest: Encodable {
    let refreshToken: String
}

struct PasskeyVerifyRequest: Encodable {
    let challenge: String
}

struct PasskeyRegisterRequest: Encodable {
    let userId: String
}

struct AuthResponse: Decodable {
    let accessToken: String
    let refreshToken: String
    let user: UserDTO
}

struct UserDTO: Decodable {
    let id: String
    let email: String
    let displayName: String
    let avatarUrl: String?
    let isVerified: Bool

    func toDomain() -> User {
        User(
            id: id,
            email: email,
            displayName: displayName,
            avatarURL: avatarUrl.flatMap(URL.init(string:)),
            isVerified: isVerified
        )
    }
}

struct PasskeyChallengeResponse: Decodable {
    let challenge: String
}
