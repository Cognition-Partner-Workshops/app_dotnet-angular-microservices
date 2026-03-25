package com.enterprise.core.observability.di

import com.enterprise.core.domain.interfaces.AnalyticsTracker
import com.enterprise.core.domain.interfaces.CrashReporter
import com.enterprise.core.observability.FirebaseAnalyticsTracker
import com.enterprise.core.observability.FirebaseCrashReporter
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** Hilt DI module binding Domain interfaces to Infrastructure implementations. */
@Module
@InstallIn(SingletonComponent::class)
abstract class ObservabilityModule {

    @Binds
    @Singleton
    abstract fun bindCrashReporter(impl: FirebaseCrashReporter): CrashReporter

    @Binds
    @Singleton
    abstract fun bindAnalyticsTracker(impl: FirebaseAnalyticsTracker): AnalyticsTracker
}
