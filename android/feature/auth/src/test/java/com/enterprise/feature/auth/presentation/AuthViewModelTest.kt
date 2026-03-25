package com.enterprise.feature.auth.presentation

import com.enterprise.core.domain.entities.DomainError
import com.enterprise.core.domain.entities.DomainResult
import com.enterprise.core.domain.entities.User
import com.enterprise.core.domain.interfaces.AnalyticsTracker
import com.enterprise.core.domain.usecases.LoginUseCase
import com.enterprise.feature.auth.domain.entities.AuthState
import com.enterprise.feature.auth.domain.entities.BiometricType
import com.enterprise.feature.auth.domain.repositories.AuthFeatureRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeAuthRepository: FakeAuthFeatureRepository
    private lateinit var fakeAnalyticsTracker: FakeAnalyticsTracker
    private lateinit var loginUseCase: LoginUseCase

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeAuthRepository = FakeAuthFeatureRepository()
        fakeAnalyticsTracker = FakeAnalyticsTracker()
        loginUseCase = LoginUseCase(fakeAuthRepository, fakeAnalyticsTracker)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `email changed updates state`() = runTest {
        val viewModel = AuthViewModel(loginUseCase, fakeAuthRepository, fakeAnalyticsTracker)
        advanceUntilIdle()

        viewModel.processIntent(AuthViewModel.Intent.EmailChanged("test@example.com"))

        assertEquals("test@example.com", viewModel.state.value.email)
    }

    @Test
    fun `password changed updates state`() = runTest {
        val viewModel = AuthViewModel(loginUseCase, fakeAuthRepository, fakeAnalyticsTracker)
        advanceUntilIdle()

        viewModel.processIntent(AuthViewModel.Intent.PasswordChanged("secret123"))

        assertEquals("secret123", viewModel.state.value.password)
    }

    @Test
    fun `login success updates auth state`() = runTest {
        val expectedUser = User(id = "1", email = "test@example.com", displayName = "Test")
        fakeAuthRepository.loginResult = DomainResult.Success(expectedUser)

        val viewModel = AuthViewModel(loginUseCase, fakeAuthRepository, fakeAnalyticsTracker)
        advanceUntilIdle()

        viewModel.processIntent(AuthViewModel.Intent.EmailChanged("test@example.com"))
        viewModel.processIntent(AuthViewModel.Intent.PasswordChanged("password"))
        viewModel.processIntent(AuthViewModel.Intent.LoginTapped)
        advanceUntilIdle()

        assertTrue(viewModel.state.value.authState is AuthState.Authenticated)
    }

    @Test
    fun `login failure updates auth state`() = runTest {
        fakeAuthRepository.loginResult = DomainResult.Failure(DomainError.Unauthorized)

        val viewModel = AuthViewModel(loginUseCase, fakeAuthRepository, fakeAnalyticsTracker)
        advanceUntilIdle()

        viewModel.processIntent(AuthViewModel.Intent.LoginTapped)
        advanceUntilIdle()

        assertTrue(viewModel.state.value.authState is AuthState.Failed)
    }
}

// Handwritten Fakes

private class FakeAuthFeatureRepository : AuthFeatureRepository {
    var loginResult: DomainResult<User> = DomainResult.Failure(DomainError.Unknown("Not set"))

    override suspend fun login(email: String, password: String) = loginResult
    override suspend fun loginWithBiometric() = loginResult
    override suspend fun loginWithPasskey(challenge: ByteArray) = loginResult
    override suspend fun logout() = DomainResult.Success(Unit)
    override suspend fun getCurrentUser() = loginResult
    override suspend fun refreshToken() = DomainResult.Success(Unit)
    override suspend fun checkBiometricAvailability() = BiometricType.NONE
    override suspend fun registerPasskey(userId: String) = DomainResult.Success(ByteArray(0))
}

private class FakeAnalyticsTracker : AnalyticsTracker {
    override fun trackScreen(name: String) {}
    override fun trackEvent(name: String, properties: Map<String, Any>) {}
}
