package org.creategoodthings.vault.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class PageNavigation {
    @Serializable
    object Home : PageNavigation()

    @Serializable
    data class Storage(val storageID: String) : PageNavigation()

    @Serializable
    object Settings : PageNavigation()
}