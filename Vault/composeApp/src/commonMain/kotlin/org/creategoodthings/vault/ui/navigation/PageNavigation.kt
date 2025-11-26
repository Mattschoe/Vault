package org.creategoodthings.vault.ui.navigation

sealed class PageNavigation(val route: String) {
    object Home : PageNavigation("home")
    object Storage : PageNavigation("storage/{ID}")
    object Settings : PageNavigation("settings")

    companion object {
        fun createStorageRoute(storageID: String) = "storage/$storageID"
    }
}