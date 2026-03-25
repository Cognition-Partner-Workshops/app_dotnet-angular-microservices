package com.enterprise.core.domain.entities

/** Feature flag for A/B testing and gradual rollouts. */
data class FeatureFlag(
    val key: String,
    val variant: String = "control",
    val isEnabled: Boolean = false,
    val payload: Map<String, String> = emptyMap()
)
