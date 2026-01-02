package org.creategoodthings.vault.ui.components

import androidx.compose.runtime.Composable

@Composable
expect fun rememberNotificationPermissionLauncher(onResult: (Boolean) -> Unit): () -> Unit
