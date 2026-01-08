package org.creategoodthings.vault

import android.app.Application
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import org.creategoodthings.vault.data.repositories.OfflinePreferencesRepository
import org.creategoodthings.vault.domain.services.AndroidNotificationScheduler
import org.creategoodthings.vault.domain.services.AndroidPermissionController
import org.creategoodthings.vault.domain.services.AndroidPurchaseManager

class MyApplication : Application() {
    lateinit var appContainer: AppContainer
    private val appScope = CoroutineScope((SupervisorJob() + Dispatchers.Main))

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
        val purchaseManager = AndroidPurchaseManager()
        appContainer = AppContainer(
            _database = dbInstance,
            preferencesRepository = prefRepo,
            notificationScheduler = scheduler,
            permissionController = permissionController,
            purchaseManager = purchaseManager
        )
        appScope.launch {
            appContainer.authRepository.initialize() //TODO Also in JVM & IOS
        }
    }
}