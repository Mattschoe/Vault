package org.creategoodthings.vault.domain.repositories

import kotlinx.coroutines.flow.Flow
import org.creategoodthings.vault.data.local.ProductEntity
import org.creategoodthings.vault.data.local.StorageEntity
import org.creategoodthings.vault.domain.Product
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
interface ProductRepository {
    suspend fun insertProduct(product: Product)
    suspend fun getAllProductsOrderedByAlphabet(): Flow<List<ProductEntity>>
    fun getStorageWithProductsOrderedByBB(): Flow<Map<StorageEntity, List<ProductEntity>>>
    fun getProductsOrderedByBB(): Flow<List<ProductEntity>>
}

