package org.creategoodthings.vault

import androidx.compose.runtime.Composable
import org.creategoodthings.vault.ui.navigation.ApplicationNavigationHost
import org.creategoodthings.vault.ui.theme.VaultTheme

@Composable
fun App(appContainer: AppContainer) {
    VaultTheme {
        ApplicationNavigationHost(appContainer)
    }
}