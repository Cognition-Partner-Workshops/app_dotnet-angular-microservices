import Foundation

/// Interface for OS permission requests.
public enum PermissionStatus: Equatable, Sendable {
    case notDetermined
    case authorized
    case denied
    case restricted
}

public enum PermissionType: Equatable, Sendable {
    case contacts
    case camera
    case microphone
    case notifications
}

public protocol PermissionService: Sendable {
    func checkStatus(for permission: PermissionType) async -> PermissionStatus
    func request(_ permission: PermissionType) async -> PermissionStatus
}
