import Foundation

/// Domain-level result type for use case outputs.
public enum DomainResult<T: Sendable>: Sendable {
    case success(T)
    case failure(DomainError)
}

/// Domain-level error hierarchy.
public enum DomainError: Error, Equatable, Sendable {
    case networkUnavailable
    case unauthorized
    case notFound
    case serverError(String)
    case emptyData
    case cacheMiss
    case unknown(String)
}
