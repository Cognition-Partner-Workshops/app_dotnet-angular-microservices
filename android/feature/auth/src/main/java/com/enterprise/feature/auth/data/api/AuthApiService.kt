package com.enterprise.feature.auth.data.api

import com.enterprise.feature.auth.data.dto.AuthResponse
import com.enterprise.feature.auth.data.dto.LoginRequest
import com.enterprise.feature.auth.data.dto.PasskeyChallengeResponse
import com.enterprise.feature.auth.data.dto.PasskeyRegisterRequest
import com.enterprise.feature.auth.data.dto.PasskeyVerifyRequest
import com.enterprise.feature.auth.data.dto.RefreshRequest
import com.enterprise.feature.auth.data.dto.UserDTO
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/** Retrofit API service for auth endpoints. */
interface AuthApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("auth/refresh")
    suspend fun refreshToken(@Body request: RefreshRequest): AuthResponse

    @POST("auth/passkey/verify")
    suspend fun verifyPasskey(@Body request: PasskeyVerifyRequest): AuthResponse

    @POST("auth/passkey/register")
    suspend fun registerPasskey(@Body request: PasskeyRegisterRequest): PasskeyChallengeResponse

    @GET("auth/me")
    suspend fun getCurrentUser(): UserDTO
}
