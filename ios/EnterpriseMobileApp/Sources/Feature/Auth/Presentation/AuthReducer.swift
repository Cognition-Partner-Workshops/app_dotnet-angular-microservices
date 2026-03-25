import Foundation
import ComposableArchitecture
import CoreDomain

/// TCA Reducer for the Auth feature - predictable, Redux-like state mutations.
@Reducer
public struct AuthReducer {
    @ObservableState
    public struct State: Equatable {
        public var email: String = ""
        public var password: String = ""
        public var authState: AuthState = .idle
        public var biometricType: BiometricType = .none
        public var showPasskeyOption: Bool = false

        public init() {}
    }

    public enum Action: Equatable, Sendable {
        case emailChanged(String)
        case passwordChanged(String)
        case loginTapped
        case biometricLoginTapped
        case passkeyLoginTapped
        case loginResponse(DomainResult<User>)
        case checkBiometricAvailability
        case biometricTypeLoaded(BiometricType)
        case logoutTapped
        case logoutCompleted
    }

    @Dependency(\.authRepository) var authRepository
    @Dependency(\.analyticsTracker) var analyticsTracker

    public init() {}

    public var body: some ReducerOf<Self> {
        Reduce { state, action in
            switch action {
            case .emailChanged(let email):
                state.email = email
                return .none

            case .passwordChanged(let password):
                state.password = password
                return .none

            case .loginTapped:
                state.authState = .authenticating
                let email = state.email
                let password = state.password
                return .run { send in
                    let useCase = LoginUseCase(
                        authRepository: authRepository,
                        analyticsTracker: analyticsTracker
                    )
                    let result = await useCase.execute(email: email, password: password)
                    await send(.loginResponse(result))
                }

            case .biometricLoginTapped:
                state.authState = .authenticating
                return .run { send in
                    let useCase = LoginUseCase(
                        authRepository: authRepository,
                        analyticsTracker: analyticsTracker
                    )
                    let result = await useCase.executeWithBiometric()
                    await send(.loginResponse(result))
                }

            case .passkeyLoginTapped:
                state.authState = .authenticating
                return .run { send in
                    let challenge = Data()  // In production: obtain from server
                    let useCase = LoginUseCase(
                        authRepository: authRepository,
                        analyticsTracker: analyticsTracker
                    )
                    let result = await useCase.executeWithPasskey(challenge: challenge)
                    await send(.loginResponse(result))
                }

            case .loginResponse(let result):
                switch result {
                case .success(let user):
                    state.authState = .authenticated(user)
                case .failure(let error):
                    state.authState = .failed(error)
                }
                return .none

            case .checkBiometricAvailability:
                return .run { send in
                    let type = await authRepository.checkBiometricAvailability()
                    await send(.biometricTypeLoaded(type))
                }

            case .biometricTypeLoaded(let type):
                state.biometricType = type
                return .none

            case .logoutTapped:
                return .run { send in
                    _ = await authRepository.logout()
                    await send(.logoutCompleted)
                }

            case .logoutCompleted:
                state.authState = .idle
                state.email = ""
                state.password = ""
                return .none
            }
        }
    }
}

// MARK: - Dependencies

private enum AuthRepositoryKey: DependencyKey {
    static let liveValue: any AuthFeatureRepository = UnimplementedAuthRepository()
}

private enum AnalyticsTrackerKey: DependencyKey {
    static let liveValue: any AnalyticsTracker = UnimplementedAnalyticsTracker()
}

extension DependencyValues {
    var authRepository: any AuthFeatureRepository {
        get { self[AuthRepositoryKey.self] }
        set { self[AuthRepositoryKey.self] = newValue }
    }

    var analyticsTracker: any AnalyticsTracker {
        get { self[AnalyticsTrackerKey.self] }
        set { self[AnalyticsTrackerKey.self] = newValue }
    }
}

// MARK: - Unimplemented Stubs

private struct UnimplementedAuthRepository: AuthFeatureRepository {
    func login(email: String, password: String) async -> DomainResult<User> { .failure(.unknown("unimplemented")) }
    func loginWithBiometric() async -> DomainResult<User> { .failure(.unknown("unimplemented")) }
    func loginWithPasskey(challenge: Data) async -> DomainResult<User> { .failure(.unknown("unimplemented")) }
    func logout() async -> DomainResult<Void> { .failure(.unknown("unimplemented")) }
    func getCurrentUser() async -> DomainResult<User> { .failure(.unknown("unimplemented")) }
    func refreshToken() async -> DomainResult<Void> { .failure(.unknown("unimplemented")) }
    func checkBiometricAvailability() async -> BiometricType { .none }
    func registerPasskey(userId: String) async -> DomainResult<Data> { .failure(.unknown("unimplemented")) }
}

private struct UnimplementedAnalyticsTracker: AnalyticsTracker {
    func trackScreen(_ name: String) {}
    func trackEvent(_ name: String, properties: [String: Any]) {}
}
