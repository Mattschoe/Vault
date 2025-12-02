package org.creategoodthings.vault

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import org.creategoodthings.vault.data.local.AppDatabase
import org.creategoodthings.vault.data.repositories.OfflinePreferencesRepository
import org.creategoodthings.vault.data.repositories.OfflineProductRepository
import org.creategoodthings.vault.domain.repositories.PreferencesRepository
import org.creategoodthings.vault.domain.repositories.ProductRepository

class AppContainer(
    private val database: AppDatabase,
    private val dataStore: DataStore<Preferences>
) {
    val productRepo: ProductRepository by lazy {
        OfflineProductRepository(database.productDao())
    }
    val preferencesRepository: PreferencesRepository by lazy {
        OfflinePreferencesRepository(dataStore)
    }
}