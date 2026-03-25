package com.enterprise.core.observability

import com.enterprise.core.domain.interfaces.CrashReporter
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Firebase Crashlytics implementation of CrashReporter.
 * No SDK bleed: Crashlytics API stays here, never leaks to Domain/Presentation.
 */
@Singleton
class FirebaseCrashReporter @Inject constructor() : CrashReporter {

    override fun logNonFatal(exception: Exception) {
        // Firebase.crashlytics.recordException(exception)
        // Placeholder: actual Firebase SDK call in production
        android.util.Log.e(TAG, "Non-fatal: ${exception.message}", exception)
    }

    override fun setCustomKey(key: String, value: String) {
        // Firebase.crashlytics.setCustomKey(key, value)
        android.util.Log.d(TAG, "Custom key: $key = $value")
    }

    override fun logBreadcrumb(message: String) {
        // Firebase.crashlytics.log(message)
        android.util.Log.d(TAG, "Breadcrumb: $message")
    }

    companion object {
        private const val TAG = "CrashReporter"
    }
}
