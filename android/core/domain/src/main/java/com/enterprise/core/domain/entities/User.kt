package com.enterprise.core.domain.entities

/** Core User entity - pure domain model with no OS dependencies. */
data class User(
    val id: String,
    val email: String,
    val displayName: String,
    val avatarUrl: String? = null,
    val isVerified: Boolean = false
)
