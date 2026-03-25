import XCTest
import ComposableArchitecture
@testable import CoreDomain
@testable import FeatureAuth

final class AuthReducerTests: XCTestCase {
    func testLoginSuccess() async {
        let testUser = User(id: "1", email: "test@example.com", displayName: "Test User", isVerified: true)

        let store = TestStore(initialState: AuthReducer.State()) {
            AuthReducer()
        } withDependencies: {
            $0.authRepository = FakeAuthRepository(loginResult: .success(testUser))
            $0.analyticsTracker = FakeAnalyticsTracker()
        }

        await store.send(.emailChanged("test@example.com")) {
            $0.email = "test@example.com"
        }
        await store.send(.passwordChanged("password123")) {
            $0.password = "password123"
        }
        await store.send(.loginTapped) {
            $0.authState = .authenticating
        }
        await store.receive(.loginResponse(.success(testUser))) {
            $0.authState = .authenticated(testUser)
        }
    }

    func testLoginFailure() async {
        let store = TestStore(initialState: AuthReducer.State()) {
            AuthReducer()
        } withDependencies: {
            $0.authRepository = FakeAuthRepository(loginResult: .failure(.unauthorized))
            $0.analyticsTracker = FakeAnalyticsTracker()
        }

        await store.send(.emailChanged("bad@example.com")) {
            $0.email = "bad@example.com"
        }
        await store.send(.passwordChanged("wrong")) {
            $0.password = "wrong"
        }
        await store.send(.loginTapped) {
            $0.authState = .authenticating
        }
        await store.receive(.loginResponse(.failure(.unauthorized))) {
            $0.authState = .failed(.unauthorized)
        }
    }

    func testLogout() async {
        let testUser = User(id: "1", email: "test@example.com", displayName: "Test User")
        var initialState = AuthReducer.State()
        initialState.authState = .authenticated(testUser)

        let store = TestStore(initialState: initialState) {
            AuthReducer()
        } withDependencies: {
            $0.authRepository = FakeAuthRepository(loginResult: .success(testUser))
            $0.analyticsTracker = FakeAnalyticsTracker()
        }

        await store.send(.logoutTapped)
        await store.receive(.logoutCompleted) {
            $0.authState = .idle
            $0.email = ""
            $0.password = ""
        }
    }
}

// MARK: - Fakes (Handwritten, not mocks)

private struct FakeAuthRepository: AuthFeatureRepository {
    let loginResult: DomainResult<User>

    func login(email: String, password: String) async -> DomainResult<User> { loginResult }
    func loginWithBiometric() async -> DomainResult<User> { loginResult }
    func loginWithPasskey(challenge: Data) async -> DomainResult<User> { loginResult }
    func logout() async -> DomainResult<Void> { .success(()) }
    func getCurrentUser() async -> DomainResult<User> { loginResult }
    func refreshToken() async -> DomainResult<Void> { .success(()) }
    func checkBiometricAvailability() async -> BiometricType { .faceID }
    func registerPasskey(userId: String) async -> DomainResult<Data> { .success(Data()) }
}

private struct FakeAnalyticsTracker: AnalyticsTracker {
    func trackScreen(_ name: String) {}
    func trackEvent(_ name: String, properties: [String: Any]) {}
}
