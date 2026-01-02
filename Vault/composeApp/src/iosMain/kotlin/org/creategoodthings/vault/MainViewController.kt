package org.creategoodthings.vault

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import org.creategoodthings.vault.domain.services.IOSNotificationScheduler
import org.creategoodthings.vault.domain.services.IOSPermissionController

fun MainViewController() = ComposeUIViewController {
    val appContainer = remember {
        val builder = getDatabaseBuilder()

        val dbInstance = builder
            .setDriver(BundledSQLiteDriver())
            .build()
        val permissionController = IOSPermissionController()
        val notificationScheduler = IOSNotificationScheduler()
        AppContainer(
            database = dbInstance,
            dataStore = createDataStore(),
            notificationScheduler = notificationScheduler,
            permissionController = permissionController
        )
    }

    App(appContainer)
}