package com.enterprise.core.domain.repositories

import com.enterprise.core.domain.entities.FeatureFlag

/** Feature flag repository contract for A/B testing. */
interface FeatureFlagRepository {
    suspend fun getFlag(key: String): FeatureFlag
    suspend fun getAllFlags(): List<FeatureFlag>
}
