package com.enterprise.core.domain.interfaces

/** OS permission request interface with rationale support. */
interface PermissionService {
    suspend fun requestContactsPermission(): PermissionResult
    suspend fun requestCameraPermission(): PermissionResult
    suspend fun requestMicrophonePermission(): PermissionResult
    suspend fun requestNotificationPermission(): PermissionResult
    fun hasPermission(permission: AppPermission): Boolean
}

enum class AppPermission {
    CONTACTS, CAMERA, MICROPHONE, NOTIFICATIONS
}

sealed class PermissionResult {
    data object Granted : PermissionResult()
    data object Denied : PermissionResult()
    data object PermanentlyDenied : PermissionResult()
}
