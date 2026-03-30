package org.creategoodthings.vault.domain.repositories

import kotlinx.coroutines.flow.Flow
import org.creategoodthings.vault.data.local.ContainerEntity
import org.creategoodthings.vault.data.local.ProductEntity
import org.creategoodthings.vault.data.local.StorageEntity
import org.creategoodthings.vault.domain.Container
import org.creategoodthings.vault.domain.ContainerID
import org.creategoodthings.vault.domain.Product
import org.creategoodthings.vault.domain.ProductID
import org.creategoodthings.vault.domain.Storage
import org.creategoodthings.vault.domain.StorageID

interface ProductRepository {
    suspend fun insertProduct(product: Product)
    suspend fun insertProducts(products: List<Product>)
    suspend fun insertStorage(storage: Storage)
    suspend fun insertContainer(container: Container)

    suspend fun updateProduct(product: Product)
    suspend fun updateStorage(storage: Storage)

    suspend fun deleteProduct(product: Product)

    fun getStoragesWithContainersShell(): Flow<Map<Storage, List<Container>>>
    fun getStorageWithProducts(storageID: String): Flow<StorageWithProducts?>
    fun getAllProductsOrderedByAlphabet(): Flow<List<Product>>
    fun getStorageContainersWithProducts(storageID: String, sortOrder: ContainerSortOrder): Flow<List<ContainerWithProducts>>
    fun getStorageProductsWithoutContainer(storageID: String, sortOrder: ContainerSortOrder): Flow<List<Product>>
    fun getProductsOrderedByBB(): Flow<List<Product>>
    fun getStorageName(storageID: String): Flow<String>
    fun getAllProducts(): Flow<List<Product>>
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
        ID = ProductID(ID),
        name = name,
        amount = amount,
        description = description,
        storageID = StorageID(storageID),
        containerID = containerID?.let { ContainerID(it) },
        bestBefore = bestBeforeDate,
        reminderDate = reminderDate,
    )
}

fun ContainerEntity.toDomain(): Container {
    return Container(
        ID = ContainerID(ID),
        storageID = storageID,
        name = name
    )
}

fun StorageEntity.toDomain(): Storage {
    return Storage(
        ID = StorageID(ID),
        name = name
    )
}
