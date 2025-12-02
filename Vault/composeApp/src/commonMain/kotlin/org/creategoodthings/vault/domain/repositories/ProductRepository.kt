package org.creategoodthings.vault.domain.repositories

import kotlinx.coroutines.flow.Flow
import org.creategoodthings.vault.data.local.ProductEntity
import org.creategoodthings.vault.domain.Container
import org.creategoodthings.vault.domain.Product
import org.creategoodthings.vault.domain.Storage

interface ProductRepository {
    suspend fun insertProduct(product: Product)
    suspend fun insertStorage(storage: Storage)
    suspend fun insertContainer(container: Container)

    fun getStoragesWithContainersShell(): Flow<Map<Storage, List<Container>>>
    fun getStorageWithProducts(storageID: String): Flow<StorageWithProducts?>
    fun getAllProductsOrderedByAlphabet(): Flow<List<Product>>
    fun getContainersWithProductsOrderedByBB(): Flow<Map<Container, List<Product>>>
    fun getStoragesWithProductsOrderedByBB(): Flow<Map<Storage, List<Product>>>
    fun getProductsOrderedByBB(): Flow<List<Product>>
}

data class StorageWithProducts(
    val storage: Storage,
    val products: List<Product>
)

fun ProductEntity.toDomain(): Product {
    return Product(
        ID = ID,
        name = name,
        amount = amount,
        description = description,
        storageID = storageID,
        containerID = containerID,
        bestBefore = bestBeforeDate,
        reminderDate = reminderDate,
    )
}

