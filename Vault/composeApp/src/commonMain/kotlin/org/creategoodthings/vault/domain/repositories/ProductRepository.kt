package org.creategoodthings.vault.domain.repositories

import kotlinx.coroutines.flow.Flow
import org.creategoodthings.vault.data.local.ProductEntity
import org.creategoodthings.vault.data.local.StorageEntity

interface ProductRepository {
    suspend fun insertProduct(product: ProductEntity)
    suspend fun getAllProducts(): Flow<List<ProductEntity>>
    fun getStorageWithProducts(): Flow<Map<StorageEntity, List<ProductEntity>>>
    fun getProductsOrderedByBB(): Flow<List<ProductEntity>>
}