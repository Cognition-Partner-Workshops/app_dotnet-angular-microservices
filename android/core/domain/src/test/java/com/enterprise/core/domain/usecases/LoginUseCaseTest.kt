package com.enterprise.core.domain.usecases

import com.enterprise.core.domain.entities.DomainError
import com.enterprise.core.domain.entities.DomainResult
import com.enterprise.core.domain.entities.User
import com.enterprise.core.domain.interfaces.AnalyticsTracker
import com.enterprise.core.domain.repositories.AuthRepository
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class LoginUseCaseTest {

    private lateinit var loginUseCase: LoginUseCase
    private lateinit var fakeAuthRepository: FakeAuthRepository
    private val fakeAnalyticsTracker = FakeAnalyticsTracker()

    @BeforeEach
    fun setUp() {
        fakeAuthRepository = FakeAuthRepository()
        loginUseCase = LoginUseCase(fakeAuthRepository, fakeAnalyticsTracker)
    }

    @Test
    fun `login with valid credentials returns success`() = runTest {
        val expectedUser = User(id = "1", email = "test@example.com", displayName = "Test User")
        fakeAuthRepository.loginResult = DomainResult.Success(expectedUser)

        val result = loginUseCase.withCredentials("test@example.com", "password123")

        assertTrue(result is DomainResult.Success)
        assertEquals(expectedUser, (result as DomainResult.Success).data)
    }

    @Test
    fun `login with invalid credentials returns failure`() = runTest {
        fakeAuthRepository.loginResult = DomainResult.Failure(DomainError.Unauthorized)

        val result = loginUseCase.withCredentials("bad@example.com", "wrong")

        assertTrue(result is DomainResult.Failure)
        assertEquals(DomainError.Unauthorized, (result as DomainResult.Failure).error)
    }

    @Test
    fun `login tracks analytics event`() = runTest {
        fakeAuthRepository.loginResult = DomainResult.Success(
            User(id = "1", email = "test@example.com", displayName = "Test")
        )

        loginUseCase.withCredentials("test@example.com", "pass")

        assertTrue(fakeAnalyticsTracker.trackedEvents.any { it.first == "login_attempt" })
        assertTrue(fakeAnalyticsTracker.trackedEvents.any { it.first == "login_success" })
    }
}

// MARK: - Handwritten Fakes (not mocks)

private class FakeAuthRepository : AuthRepository {
    var loginResult: DomainResult<User> = DomainResult.Failure(DomainError.Unknown("Not set"))

    override suspend fun login(email: String, password: String): DomainResult<User> = loginResult
    override suspend fun loginWithBiometric(): DomainResult<User> = loginResult
    override suspend fun loginWithPasskey(challenge: ByteArray): DomainResult<User> = loginResult
    override suspend fun logout(): DomainResult<Unit> = DomainResult.Success(Unit)
    override suspend fun getCurrentUser(): DomainResult<User> = loginResult
    override suspend fun refreshToken(): DomainResult<Unit> = DomainResult.Success(Unit)
}

private class FakeAnalyticsTracker : AnalyticsTracker {
    val trackedEvents = mutableListOf<Pair<String, Map<String, Any>>>()
    val trackedScreens = mutableListOf<String>()

    override fun trackScreen(name: String) { trackedScreens.add(name) }
    override fun trackEvent(name: String, properties: Map<String, Any>) {
        trackedEvents.add(name to properties)
    }
}
