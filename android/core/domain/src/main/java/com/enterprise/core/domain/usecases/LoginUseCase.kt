package com.enterprise.core.domain.usecases

import com.enterprise.core.domain.entities.DomainResult
import com.enterprise.core.domain.entities.User
import com.enterprise.core.domain.interfaces.AnalyticsTracker
import com.enterprise.core.domain.repositories.AuthRepository
import javax.inject.Inject

/** Executes login with credentials, biometric, or passkey and tracks analytics. */
class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val analyticsTracker: AnalyticsTracker
) {
    suspend fun withCredentials(email: String, password: String): DomainResult<User> {
        analyticsTracker.trackEvent("login_attempt", mapOf("method" to "credentials"))
        val result = authRepository.login(email, password)
        when (result) {
            is DomainResult.Success -> analyticsTracker.trackEvent("login_success", mapOf("method" to "credentials"))
            is DomainResult.Failure -> analyticsTracker.trackEvent("login_failure", mapOf("method" to "credentials"))
        }
        return result
    }

    suspend fun withBiometric(): DomainResult<User> {
        analyticsTracker.trackEvent("login_attempt", mapOf("method" to "biometric"))
        return authRepository.loginWithBiometric()
    }

    suspend fun withPasskey(challenge: ByteArray): DomainResult<User> {
        analyticsTracker.trackEvent("login_attempt", mapOf("method" to "passkey"))
        return authRepository.loginWithPasskey(challenge)
    }
}
