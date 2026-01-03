package org.creategoodthings.vault

import android.app.Application
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import org.creategoodthings.vault.data.repositories.OfflinePreferencesRepository
import org.creategoodthings.vault.domain.repositories.PreferencesRepository
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
        val prefRepo = OfflinePreferencesRepository(dataStore)
        val scheduler = AndroidNotificationScheduler(
            context = this,
            prefRepo = prefRepo
        )
        val permissionController = AndroidPermissionController(this)
        appContainer = AppContainer(
            database = dbInstance,
            preferencesRepository = prefRepo,
            notificationScheduler = scheduler,
            permissionController = permissionController
        )
    }
}