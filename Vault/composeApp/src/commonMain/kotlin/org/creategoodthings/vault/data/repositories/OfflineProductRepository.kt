package org.creategoodthings.vault.data.repositories

import kotlinx.coroutines.flow.Flow
import org.creategoodthings.vault.data.local.ProductDao
import org.creategoodthings.vault.data.local.ProductEntity
import org.creategoodthings.vault.data.local.StorageEntity
import org.creategoodthings.vault.domain.repositories.ProductRepository

class OfflineProductRepository(private val dao: ProductDao): ProductRepository {
    override suspend fun insertProduct(product: ProductEntity) {
        dao.insertProduct(product)
    }

    override suspend fun getAllProducts(): Flow<List<ProductEntity>> {
        return dao.getAllProducts()
    }

    override fun getStorageWithProducts(): Flow<Map<StorageEntity, List<ProductEntity>>> {
        return dao.getStoragesWithProducts()
    }

    override fun getProductsOrderedByBB(): Flow<List<ProductEntity>> {
        return dao.getProductsOrderedByBB()
    }
}