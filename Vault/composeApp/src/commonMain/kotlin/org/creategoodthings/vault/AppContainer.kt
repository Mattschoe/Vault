package org.creategoodthings.vault

import org.creategoodthings.vault.data.local.AppDatabase
import org.creategoodthings.vault.data.repositories.OfflineProductRepository
import org.creategoodthings.vault.domain.repositories.ProductRepository

class AppContainer(private val database: AppDatabase) {
    val productRepo: ProductRepository by lazy {
        OfflineProductRepository(database.productDao())
    }
}