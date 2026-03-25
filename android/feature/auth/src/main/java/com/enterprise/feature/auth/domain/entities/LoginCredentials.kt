package com.enterprise.feature.auth.domain.entities

/** Credentials for email/password login. */
data class LoginCredentials(
    val email: String,
    val password: String
)
