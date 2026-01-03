package org.creategoodthings.vault

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import org.creategoodthings.vault.data.repositories.OfflinePreferencesRepository
import org.creategoodthings.vault.domain.services.IOSNotificationScheduler
import org.creategoodthings.vault.domain.services.IOSPermissionController

fun MainViewController() = ComposeUIViewController {
    val appContainer = remember {
        val builder = getDatabaseBuilder()

        val dbInstance = builder
            .setDriver(BundledSQLiteDriver())
            .build()
        val permissionController = IOSPermissionController()
        val dataStore = createDataStore()
        val prefRepo = OfflinePreferencesRepository(dataStore)
        val notificationScheduler = IOSNotificationScheduler(prefRepo)
        AppContainer(
            database = dbInstance,
            preferencesRepository = prefRepo,
            notificationScheduler = notificationScheduler,
            permissionController = permissionController
        )
    }

    App(appContainer)
}