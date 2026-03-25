package com.enterprise.core.domain.repositories

import com.enterprise.core.domain.entities.DomainResult
import com.enterprise.core.domain.entities.User

/** Authentication repository contract - no OS/framework dependencies. */
interface AuthRepository {
    suspend fun login(email: String, password: String): DomainResult<User>
    suspend fun loginWithBiometric(): DomainResult<User>
    suspend fun loginWithPasskey(challenge: ByteArray): DomainResult<User>
    suspend fun logout(): DomainResult<Unit>
    suspend fun getCurrentUser(): DomainResult<User>
    suspend fun refreshToken(): DomainResult<Unit>
}
