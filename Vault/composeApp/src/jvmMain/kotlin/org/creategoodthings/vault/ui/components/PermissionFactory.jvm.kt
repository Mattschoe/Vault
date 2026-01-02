package org.creategoodthings.vault.ui.components

import androidx.compose.runtime.Composable

@Composable
actual fun rememberNotificationPermissionLauncher(onResult: (Boolean) -> Unit): () -> Unit {
    return { onResult(false) } //No notifications on laptop
}