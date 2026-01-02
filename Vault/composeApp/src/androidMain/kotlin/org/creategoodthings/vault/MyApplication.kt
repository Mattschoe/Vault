package org.creategoodthings.vault

import android.app.Application
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import org.creategoodthings.vault.domain.services.AndroidNotificationScheduler
import org.creategoodthings.vault.domain.services.AndroidPermissionController

class MyApplication : Application() {
    lateinit var appContainer: AppContainer

    override fun onCreate() {
        super.onCreate()

        val builder = getDatabaseBuilder(applicationContext)
        val dbInstance = builder
            .setDriver(BundledSQLiteDriver())
            .build()

        val dataStore = createDataStore(this)
        val scheduler = AndroidNotificationScheduler(this)
        val permissionController = AndroidPermissionController(this)
        appContainer = AppContainer(
            database = dbInstance,
            dataStore = dataStore,
            notificationScheduler = scheduler,
            permissionController = permissionController
        )
    }
}