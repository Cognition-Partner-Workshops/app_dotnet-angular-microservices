import Foundation
import CoreDomain

/// Feature-specific repository extensions for Auth.
public protocol AuthFeatureRepository: AuthRepository {
    func checkBiometricAvailability() async -> BiometricType
    func registerPasskey(userId: String) async -> DomainResult<Data>
}
