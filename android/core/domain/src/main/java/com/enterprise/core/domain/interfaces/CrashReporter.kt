package com.enterprise.core.domain.interfaces

/** Crash reporting interface - implemented in Infrastructure layer. No SDK bleed. */
interface CrashReporter {
    fun logNonFatal(exception: Exception)
    fun setCustomKey(key: String, value: String)
    fun logBreadcrumb(message: String)
}
