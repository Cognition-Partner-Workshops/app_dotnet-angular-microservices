package com.enterprise.feature.auth.domain.entities

import com.enterprise.core.domain.entities.DomainError
import com.enterprise.core.domain.entities.User

/** Authentication state for the Auth feature MVI pattern. */
sealed class AuthState {
    data object Idle : AuthState()
    data object Authenticating : AuthState()
    data class Authenticated(val user: User) : AuthState()
    data class Failed(val error: DomainError) : AuthState()
}
