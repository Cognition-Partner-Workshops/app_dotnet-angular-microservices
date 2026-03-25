package com.enterprise.feature.auth.data.dto

import com.enterprise.core.domain.entities.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class RefreshRequest(
    @SerialName("refresh_token") val refreshToken: String
)

@Serializable
data class PasskeyVerifyRequest(
    val challenge: String,
    val response: String
)

@Serializable
data class PasskeyRegisterRequest(
    @SerialName("user_id") val userId: String
)

@Serializable
data class AuthResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String,
    val user: UserDTO
)

@Serializable
data class UserDTO(
    val id: String,
    val email: String,
    @SerialName("display_name") val displayName: String,
    @SerialName("avatar_url") val avatarUrl: String? = null,
    @SerialName("is_verified") val isVerified: Boolean = false
) {
    fun toDomain(): User = User(
        id = id,
        email = email,
        displayName = displayName,
        avatarUrl = avatarUrl,
        isVerified = isVerified
    )
}

@Serializable
data class PasskeyChallengeResponse(
    val challenge: String,
    @SerialName("rp_id") val rpId: String,
    @SerialName("user_id") val userId: String
)
