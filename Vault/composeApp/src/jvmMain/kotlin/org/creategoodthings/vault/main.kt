package org.creategoodthings.vault

import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import org.creategoodthings.vault.data.local.DATA_STORE_FILE_NAME
import org.creategoodthings.vault.data.local.createDataStore
import org.creategoodthings.vault.data.local.getDatabaseBuilder
import org.creategoodthings.vault.data.repositories.OfflinePreferencesRepository
import org.creategoodthings.vault.domain.services.JvmNotificationScheduler
import org.creategoodthings.vault.domain.services.JvmPermissionController

fun main() = application {
    val appContainer = remember {
        val builder = getDatabaseBuilder()
        val db = builder
            .setDriver(BundledSQLiteDriver())
            .build()
        val dataStore = createDataStore { DATA_STORE_FILE_NAME }
        val prefRepo = OfflinePreferencesRepository(dataStore)
        val scheduler = JvmNotificationScheduler()
        val permissionController = JvmPermissionController()
        AppContainer(
            database = db,
            preferencesRepository = prefRepo,
            notificationScheduler = scheduler,
            permissionController = permissionController
        )
    }

    Window(
        onCloseRequest = ::exitApplication,
        title = "Vault",
    ) {
        App(appContainer)
    }
}