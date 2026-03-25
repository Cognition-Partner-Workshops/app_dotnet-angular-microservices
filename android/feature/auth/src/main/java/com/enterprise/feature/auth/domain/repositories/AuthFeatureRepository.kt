package com.enterprise.feature.auth.domain.repositories

import com.enterprise.core.domain.entities.DomainResult
import com.enterprise.core.domain.repositories.AuthRepository
import com.enterprise.feature.auth.domain.entities.BiometricType

/** Extended auth repository with biometric and passkey capabilities. */
interface AuthFeatureRepository : AuthRepository {
    suspend fun checkBiometricAvailability(): BiometricType
    suspend fun registerPasskey(userId: String): DomainResult<ByteArray>
}
