package com.enterprise.feature.auth.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.enterprise.core.domain.entities.DomainError
import com.enterprise.feature.auth.domain.entities.AuthState
import com.enterprise.feature.auth.domain.entities.BiometricType

/** Login screen with email/password, biometric, and passkey options. */
@Composable
fun LoginScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    // Navigate on successful auth
    if (state.authState is AuthState.Authenticated) {
        onLoginSuccess()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        Text(
            text = "Welcome Back",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.testTag("loginTitle")
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Sign in to your account",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Email field
        OutlinedTextField(
            value = state.email,
            onValueChange = { viewModel.processIntent(AuthViewModel.Intent.EmailChanged(it)) },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("emailField"),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Password field
        OutlinedTextField(
            value = state.password,
            onValueChange = { viewModel.processIntent(AuthViewModel.Intent.PasswordChanged(it)) },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("passwordField"),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Login button
        Button(
            onClick = { viewModel.processIntent(AuthViewModel.Intent.LoginTapped) },
            enabled = state.email.isNotBlank() && state.password.isNotBlank()
                    && state.authState !is AuthState.Authenticating,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("loginButton"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            if (state.authState is AuthState.Authenticating) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Sign In", fontWeight = FontWeight.Bold)
            }
        }

        // Error display
        if (state.authState is AuthState.Failed) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = errorMessage((state.authState as AuthState.Failed).error),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.testTag("errorText")
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Biometric / Passkey options
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
        ) {
            if (state.biometricType != BiometricType.NONE) {
                OutlinedButton(
                    onClick = { viewModel.processIntent(AuthViewModel.Intent.BiometricLoginTapped) },
                    modifier = Modifier.testTag("biometricButton")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Fingerprint,
                        contentDescription = "Biometric Login",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text("Biometric")
                }
            }

            if (state.showPasskeyOption) {
                OutlinedButton(
                    onClick = { viewModel.processIntent(AuthViewModel.Intent.PasskeyLoginTapped) },
                    modifier = Modifier.testTag("passkeyButton")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Key,
                        contentDescription = "Passkey Login",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text("Passkey")
                }
            }
        }
    }
}

private fun errorMessage(error: DomainError): String = when (error) {
    is DomainError.Unauthorized -> "Invalid credentials. Please try again."
    is DomainError.NetworkUnavailable -> "No network connection. Please check your internet."
    is DomainError.ServerError -> "Server error: ${error.message}"
    else -> "An unexpected error occurred."
}
