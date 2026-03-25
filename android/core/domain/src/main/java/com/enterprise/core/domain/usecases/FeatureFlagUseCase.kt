package com.enterprise.core.domain.usecases

import com.enterprise.core.domain.entities.FeatureFlag
import com.enterprise.core.domain.repositories.FeatureFlagRepository
import javax.inject.Inject

/** Determines A/B test variant for UI rendering decisions. */
class FeatureFlagUseCase @Inject constructor(
    private val featureFlagRepository: FeatureFlagRepository
) {
    suspend fun getVariant(flagKey: String): String {
        val flag = featureFlagRepository.getFlag(flagKey)
        return if (flag.isEnabled) flag.variant else "control"
    }

    suspend fun isEnabled(flagKey: String): Boolean {
        return featureFlagRepository.getFlag(flagKey).isEnabled
    }
}
