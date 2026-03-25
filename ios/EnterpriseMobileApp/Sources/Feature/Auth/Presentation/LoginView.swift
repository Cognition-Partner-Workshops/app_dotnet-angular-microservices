import SwiftUI
import ComposableArchitecture
import CoreDomain

/// Login screen driven by TCA Reducer - no business logic in the view.
public struct LoginView: View {
    @Bindable var store: StoreOf<AuthReducer>

    public init(store: StoreOf<AuthReducer>) {
        self.store = store
    }

    public var body: some View {
        VStack(spacing: 24) {
            Spacer()

            // Logo / Brand
            Image(systemName: "shield.checkered")
                .font(.system(size: 64))
                .foregroundColor(.accentColor)

            Text("Welcome Back")
                .font(.largeTitle)
                .fontWeight(.bold)

            // Email & Password
            VStack(spacing: 16) {
                TextField("Email", text: $store.email.sending(\.emailChanged))
                    .textFieldStyle(.roundedBorder)
                    .keyboardType(.emailAddress)
                    .textContentType(.emailAddress)
                    .autocapitalization(.none)
                    .accessibilityIdentifier("emailField")

                SecureField("Password", text: $store.password.sending(\.passwordChanged))
                    .textFieldStyle(.roundedBorder)
                    .textContentType(.password)
                    .accessibilityIdentifier("passwordField")
            }
            .padding(.horizontal, 32)

            // Login Button
            Button(action: { store.send(.loginTapped) }) {
                Group {
                    if store.authState == .authenticating {
                        ProgressView()
                            .tint(.white)
                    } else {
                        Text("Sign In")
                            .fontWeight(.semibold)
                    }
                }
                .frame(maxWidth: .infinity)
                .padding()
                .background(Color.accentColor)
                .foregroundColor(.white)
                .cornerRadius(12)
            }
            .disabled(store.authState == .authenticating)
            .padding(.horizontal, 32)
            .accessibilityIdentifier("signInButton")

            // Biometric Login
            if store.biometricType != .none {
                Button(action: { store.send(.biometricLoginTapped) }) {
                    HStack {
                        Image(systemName: store.biometricType == .faceID ? "faceid" : "touchid")
                        Text(store.biometricType == .faceID ? "Sign in with Face ID" : "Sign in with Touch ID")
                    }
                    .foregroundColor(.accentColor)
                }
                .accessibilityIdentifier("biometricButton")
            }

            // Passkey Login
            if store.showPasskeyOption {
                Button(action: { store.send(.passkeyLoginTapped) }) {
                    HStack {
                        Image(systemName: "key.fill")
                        Text("Sign in with Passkey")
                    }
                    .foregroundColor(.accentColor)
                }
                .accessibilityIdentifier("passkeyButton")
            }

            // Error Display
            if case .failed(let error) = store.authState {
                Text(errorMessage(for: error))
                    .font(.caption)
                    .foregroundColor(.red)
                    .padding(.horizontal, 32)
                    .accessibilityIdentifier("errorLabel")
            }

            Spacer()
        }
        .onAppear { store.send(.checkBiometricAvailability) }
    }

    private func errorMessage(for error: DomainError) -> String {
        switch error {
        case .unauthorized: return "Invalid credentials. Please try again."
        case .networkUnavailable: return "No internet connection. Please check your network."
        case .serverError(let msg): return "Server error: \(msg)"
        default: return "An unexpected error occurred."
        }
    }
}
