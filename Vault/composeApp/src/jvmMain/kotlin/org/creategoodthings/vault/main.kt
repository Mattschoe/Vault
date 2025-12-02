package org.creategoodthings.vault

import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import org.creategoodthings.vault.data.local.DATA_STORE_FILE_NAME
import org.creategoodthings.vault.data.local.createDataStore
import org.creategoodthings.vault.data.local.getDatabaseBuilder

fun main() = application {
    val appContainer = remember {
        val builder = getDatabaseBuilder()
        val db = builder
            .setDriver(BundledSQLiteDriver()) // Don't forget this!
            .build()
        val dataStore = createDataStore { DATA_STORE_FILE_NAME }

        AppContainer(
            database = db,
            dataStore = dataStore
        )
    }

    Window(
        onCloseRequest = ::exitApplication,
        title = "Vault",
    ) {
        App(appContainer)
    }
}