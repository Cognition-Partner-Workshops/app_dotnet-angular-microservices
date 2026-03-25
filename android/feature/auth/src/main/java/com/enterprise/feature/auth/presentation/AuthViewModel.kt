package com.enterprise.feature.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enterprise.core.domain.entities.DomainResult
import com.enterprise.core.domain.interfaces.AnalyticsTracker
import com.enterprise.core.domain.usecases.LoginUseCase
import com.enterprise.feature.auth.domain.entities.AuthState
import com.enterprise.feature.auth.domain.entities.BiometricType
import com.enterprise.feature.auth.domain.repositories.AuthFeatureRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** MVI ViewModel for Auth feature with State and Intent pattern. */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val authRepository: AuthFeatureRepository,
    private val analyticsTracker: AnalyticsTracker
) : ViewModel() {

    data class State(
        val email: String = "",
        val password: String = "",
        val authState: AuthState = AuthState.Idle,
        val biometricType: BiometricType = BiometricType.NONE,
        val showPasskeyOption: Boolean = false
    )

    sealed class Intent {
        data class EmailChanged(val email: String) : Intent()
        data class PasswordChanged(val password: String) : Intent()
        data object LoginTapped : Intent()
        data object BiometricLoginTapped : Intent()
        data object PasskeyLoginTapped : Intent()
        data object LogoutTapped : Intent()
        data object CheckBiometricAvailability : Intent()
    }

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()

    init {
        processIntent(Intent.CheckBiometricAvailability)
    }

    fun processIntent(intent: Intent) {
        when (intent) {
            is Intent.EmailChanged -> _state.update { it.copy(email = intent.email) }
            is Intent.PasswordChanged -> _state.update { it.copy(password = intent.password) }
            is Intent.LoginTapped -> performLogin()
            is Intent.BiometricLoginTapped -> performBiometricLogin()
            is Intent.PasskeyLoginTapped -> performPasskeyLogin()
            is Intent.LogoutTapped -> performLogout()
            is Intent.CheckBiometricAvailability -> checkBiometric()
        }
    }

    private fun performLogin() {
        viewModelScope.launch {
            _state.update { it.copy(authState = AuthState.Authenticating) }
            analyticsTracker.trackScreen("login")
            val result = loginUseCase.withCredentials(
                _state.value.email,
                _state.value.password
            )
            _state.update { currentState ->
                when (result) {
                    is DomainResult.Success -> currentState.copy(
                        authState = AuthState.Authenticated(result.data)
                    )
                    is DomainResult.Failure -> currentState.copy(
                        authState = AuthState.Failed(result.error)
                    )
                }
            }
        }
    }

    private fun performBiometricLogin() {
        viewModelScope.launch {
            _state.update { it.copy(authState = AuthState.Authenticating) }
            val result = loginUseCase.withBiometric()
            _state.update { currentState ->
                when (result) {
                    is DomainResult.Success -> currentState.copy(
                        authState = AuthState.Authenticated(result.data)
                    )
                    is DomainResult.Failure -> currentState.copy(
                        authState = AuthState.Failed(result.error)
                    )
                }
            }
        }
    }

    private fun performPasskeyLogin() {
        viewModelScope.launch {
            _state.update { it.copy(authState = AuthState.Authenticating) }
            val result = loginUseCase.withPasskey(ByteArray(0))
            _state.update { currentState ->
                when (result) {
                    is DomainResult.Success -> currentState.copy(
                        authState = AuthState.Authenticated(result.data)
                    )
                    is DomainResult.Failure -> currentState.copy(
                        authState = AuthState.Failed(result.error)
                    )
                }
            }
        }
    }

    private fun performLogout() {
        viewModelScope.launch {
            authRepository.logout()
            _state.update {
                State() // Reset to initial state
            }
        }
    }

    private fun checkBiometric() {
        viewModelScope.launch {
            val biometricType = authRepository.checkBiometricAvailability()
            _state.update {
                it.copy(
                    biometricType = biometricType,
                    showPasskeyOption = true
                )
            }
        }
    }
}
