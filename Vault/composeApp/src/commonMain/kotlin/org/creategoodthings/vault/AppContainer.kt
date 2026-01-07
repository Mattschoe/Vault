package org.creategoodthings.vault

import org.creategoodthings.vault.data.local.AppDatabase
import org.creategoodthings.vault.data.network.createHttpClient
import org.creategoodthings.vault.data.repositories.KtorAuthRepository
import org.creategoodthings.vault.data.repositories.OfflineProductRepository
import org.creategoodthings.vault.domain.repositories.PreferencesRepository
import org.creategoodthings.vault.domain.repositories.ProductRepository
import org.creategoodthings.vault.domain.services.NotificationScheduler
import org.creategoodthings.vault.domain.services.PermissionController
import org.creategoodthings.vault.domain.services.PurchaseManager

class AppContainer(
    private val _database: AppDatabase,
    val preferencesRepository: PreferencesRepository,
    val notificationScheduler: NotificationScheduler,
    val permissionController: PermissionController,
    val purchaseManager: PurchaseManager
) {
    val productRepo: ProductRepository by lazy {
        OfflineProductRepository(_database.productDao())
    }

    val httpClient by lazy {
        createHttpClient()
    }

    val authRepository = KtorAuthRepository(
        _client = httpClient,
        _prefRepo = preferencesRepository,
        _purchaseManager = purchaseManager
    )
}