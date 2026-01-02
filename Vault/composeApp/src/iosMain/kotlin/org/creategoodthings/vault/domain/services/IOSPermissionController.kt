package org.creategoodthings.vault.domain.services

import platform.UserNotifications.UNAuthorizationStatusAuthorized
import platform.UserNotifications.UNUserNotificationCenter
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class IOSPermissionController : PermissionController {
    override suspend fun hasNotificationPermission(): Boolean = suspendCoroutine { continuation ->
        UNUserNotificationCenter.currentNotificationCenter().getNotificationSettingsWithCompletionHandler { settings ->
            val status = settings?.authorizationStatus
            val isGranted = status == UNAuthorizationStatusAuthorized
            continuation.resume(isGranted)
        }
    }
}