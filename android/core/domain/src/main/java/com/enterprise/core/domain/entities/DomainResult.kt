package com.enterprise.core.domain.entities

/** Domain-level result type for use case outputs. */
sealed class DomainResult<out T> {
    data class Success<T>(val data: T) : DomainResult<T>()
    data class Failure(val error: DomainError) : DomainResult<Nothing>()
}

/** Domain-level error hierarchy. */
sealed class DomainError {
    data object NetworkUnavailable : DomainError()
    data object Unauthorized : DomainError()
    data object NotFound : DomainError()
    data class ServerError(val message: String) : DomainError()
    data object EmptyData : DomainError()
    data object CacheMiss : DomainError()
    data class Unknown(val message: String) : DomainError()
}
