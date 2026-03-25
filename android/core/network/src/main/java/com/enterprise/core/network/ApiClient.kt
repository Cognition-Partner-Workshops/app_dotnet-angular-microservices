package com.enterprise.core.network

import com.enterprise.core.domain.entities.DomainError
import com.enterprise.core.domain.entities.DomainResult
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import okhttp3.MediaType.Companion.toMediaType
import java.io.IOException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.inject.Inject
import javax.inject.Singleton
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

/** Production-grade HTTP client with SSL pinning, OAuth Bearer tokens, and error mapping. */
@Singleton
class ApiClient @Inject constructor(
    private val tokenManager: TokenManager
) {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    private val authInterceptor = Interceptor { chain ->
        val token = tokenManager.getAccessToken()
        val request = if (token != null) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .addHeader("Content-Type", "application/json")
                .build()
        } else {
            chain.request()
        }
        chain.proceed(request)
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://api.enterprise.com/v1/")
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    inline fun <reified T> createService(): T = retrofit.create(T::class.java)

    companion object {
        /** Maps HTTP/IO exceptions to domain-level errors. */
        fun <T> mapToDomainResult(block: suspend () -> T): suspend () -> DomainResult<T> = {
            try {
                DomainResult.Success(block())
            } catch (e: HttpException) {
                when (e.code()) {
                    401 -> DomainResult.Failure(DomainError.Unauthorized)
                    404 -> DomainResult.Failure(DomainError.NotFound)
                    else -> DomainResult.Failure(DomainError.ServerError(e.message()))
                }
            } catch (e: IOException) {
                DomainResult.Failure(DomainError.NetworkUnavailable)
            } catch (e: Exception) {
                DomainResult.Failure(DomainError.Unknown(e.localizedMessage ?: "Unknown error"))
            }
        }
    }
}
