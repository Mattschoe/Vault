package org.creategoodthings.vault.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNUserNotificationCenter

@Composable
actual fun rememberNotificationPermissionLauncher(onResult: (Boolean) -> Unit): () -> Unit {
    return remember {
        {
            val center = UNUserNotificationCenter.currentNotificationCenter()
            val options = UNAuthorizationOptionAlert or UNAuthorizationOptionSound or UNAuthorizationOptionBadge
            center.requestAuthorizationWithOptions(options) { granted, error ->
                onResult(granted)
                if (error != null) println("Permission error: $error")
            }

        }
    }
}