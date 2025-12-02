package org.creategoodthings.vault

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController

fun MainViewController() = ComposeUIViewController {
    val appContainer = remember {
        val builder = getDatabaseBuilder()

        val dbInstance = builder
            .setDriver(BundledSQLiteDriver())
            .build()
        AppContainer(
            database = dbInstance,
            dataStore = createDataStore()
        )
    }

    App(appContainer)
}