package org.creategoodthings.vault.domain.repositories

import kotlinx.coroutines.flow.Flow
import org.creategoodthings.vault.data.local.ContainerEntity
import org.creategoodthings.vault.data.local.ProductEntity
import org.creategoodthings.vault.domain.Container
import org.creategoodthings.vault.domain.Product
import org.creategoodthings.vault.domain.Storage

interface ProductRepository {
    suspend fun insertProduct(product: Product)
    suspend fun insertStorage(storage: Storage)
    suspend fun insertContainer(container: Container)

    suspend fun updateProduct(product: Product)
    suspend fun updateStorage(storage: Storage)

    suspend fun deleteProduct(product: Product)

    fun getStoragesWithContainersShell(): Flow<Map<Storage, List<Container>>>
    fun getStorageWithProducts(storageID: String): Flow<StorageWithProducts?>
    fun getAllProductsOrderedByAlphabet(): Flow<List<Product>>
    fun getStorageContainersWithProductsOrderedByBB(storageID: String): Flow<List<ContainerWithProducts>>
    fun getStorageProductsWithoutContainerOrderedByBB(storageID: String): Flow<List<Product>>
    fun getProductsOrderedByBB(): Flow<List<Product>>
    fun getStorageName(storageID: String): Flow<String>
}

data class StorageWithProducts(
    val storage: Storage,
    val products: List<Product>
)

data class ContainerWithProducts(
    val container: Container,
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

fun ContainerEntity.toDomain(): Container {
    return Container(
        ID = ID,
        storageID = storageID,
        name = name
    )
}
