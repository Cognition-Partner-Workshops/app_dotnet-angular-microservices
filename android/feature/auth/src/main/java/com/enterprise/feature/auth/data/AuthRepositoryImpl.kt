package com.enterprise.feature.auth.data

import android.content.Context
import androidx.biometric.BiometricManager
import com.enterprise.core.domain.entities.DomainError
import com.enterprise.core.domain.entities.DomainResult
import com.enterprise.core.domain.entities.User
import com.enterprise.core.network.TokenManager
import com.enterprise.feature.auth.data.api.AuthApiService
import com.enterprise.feature.auth.data.dto.LoginRequest
import com.enterprise.feature.auth.data.dto.PasskeyRegisterRequest
import com.enterprise.feature.auth.data.dto.PasskeyVerifyRequest
import com.enterprise.feature.auth.data.dto.RefreshRequest
import com.enterprise.feature.auth.domain.entities.BiometricType
import com.enterprise.feature.auth.domain.repositories.AuthFeatureRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/** Concrete auth repository with DTO mapping, token storage, biometric, and passkey support. */
@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authApiService: AuthApiService,
    private val tokenManager: TokenManager,
    @ApplicationContext private val context: Context
) : AuthFeatureRepository {

    override suspend fun login(email: String, password: String): DomainResult<User> {
        return try {
            val response = authApiService.login(LoginRequest(email, password))
            tokenManager.saveAccessToken(response.accessToken)
            tokenManager.saveRefreshToken(response.refreshToken)
            DomainResult.Success(response.user.toDomain())
        } catch (e: Exception) {
            DomainResult.Failure(DomainError.Unknown(e.localizedMessage ?: "Login failed"))
        }
    }

    override suspend fun loginWithBiometric(): DomainResult<User> {
        val refreshToken = tokenManager.getRefreshToken()
            ?: return DomainResult.Failure(DomainError.Unauthorized)
        return try {
            val response = authApiService.refreshToken(RefreshRequest(refreshToken))
            tokenManager.saveAccessToken(response.accessToken)
            tokenManager.saveRefreshToken(response.refreshToken)
            DomainResult.Success(response.user.toDomain())
        } catch (e: Exception) {
            DomainResult.Failure(DomainError.Unauthorized)
        }
    }

    override suspend fun loginWithPasskey(challenge: ByteArray): DomainResult<User> {
        return try {
            val challengeString = android.util.Base64.encodeToString(challenge, android.util.Base64.NO_WRAP)
            val response = authApiService.verifyPasskey(
                PasskeyVerifyRequest(challenge = challengeString, response = challengeString)
            )
            tokenManager.saveAccessToken(response.accessToken)
            tokenManager.saveRefreshToken(response.refreshToken)
            DomainResult.Success(response.user.toDomain())
        } catch (e: Exception) {
            DomainResult.Failure(DomainError.Unknown(e.localizedMessage ?: "Passkey login failed"))
        }
    }

    override suspend fun logout(): DomainResult<Unit> {
        tokenManager.clearTokens()
        return DomainResult.Success(Unit)
    }

    override suspend fun getCurrentUser(): DomainResult<User> {
        return try {
            val user = authApiService.getCurrentUser()
            DomainResult.Success(user.toDomain())
        } catch (e: Exception) {
            DomainResult.Failure(DomainError.Unauthorized)
        }
    }

    override suspend fun refreshToken(): DomainResult<Unit> {
        val refreshToken = tokenManager.getRefreshToken()
            ?: return DomainResult.Failure(DomainError.Unauthorized)
        return try {
            val response = authApiService.refreshToken(RefreshRequest(refreshToken))
            tokenManager.saveAccessToken(response.accessToken)
            tokenManager.saveRefreshToken(response.refreshToken)
            DomainResult.Success(Unit)
        } catch (e: Exception) {
            DomainResult.Failure(DomainError.Unauthorized)
        }
    }

    override suspend fun checkBiometricAvailability(): BiometricType {
        val biometricManager = BiometricManager.from(context)
        return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> BiometricType.FINGERPRINT
            else -> BiometricType.NONE
        }
    }

    override suspend fun registerPasskey(userId: String): DomainResult<ByteArray> {
        return try {
            val response = authApiService.registerPasskey(PasskeyRegisterRequest(userId))
            val challengeBytes = android.util.Base64.decode(response.challenge, android.util.Base64.NO_WRAP)
            DomainResult.Success(challengeBytes)
        } catch (e: Exception) {
            DomainResult.Failure(DomainError.Unknown(e.localizedMessage ?: "Passkey registration failed"))
        }
    }
}
