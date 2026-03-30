package org.creategoodthings.vault.data.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.creategoodthings.vault.data.local.ContainerEntity
import org.creategoodthings.vault.data.local.ProductDao
import org.creategoodthings.vault.data.local.ProductEntity
import org.creategoodthings.vault.data.local.StorageEntity
import org.creategoodthings.vault.domain.Container
import org.creategoodthings.vault.domain.Product
import org.creategoodthings.vault.domain.Storage
import org.creategoodthings.vault.domain.repositories.ContainerSortOrder
import org.creategoodthings.vault.domain.repositories.ContainerWithProducts
import org.creategoodthings.vault.domain.repositories.ProductRepository
import org.creategoodthings.vault.domain.repositories.StorageWithProducts
import org.creategoodthings.vault.domain.repositories.toDomain
import org.creategoodthings.vault.domain.repositories.ContainerSortOrder.*

class OfflineProductRepository(private val dao: ProductDao): ProductRepository {
    override suspend fun insertProduct(product: Product) {
        dao.insertProduct(ProductEntity(
            ID = product.ID.value,
            storageID = product.storageID.value,
            containerID = product.containerID?.value,
            name = product.name,
            description = product.description,
            bestBeforeDate = product.bestBefore,
            reminderDate = product.reminderDate,
            amount = product.amount,
            isDirty = true,
            isDeleted = false
        ))
    }

    override suspend fun insertProducts(products: List<Product>) {
        dao.insertProducts(products.map { product ->
            ProductEntity(
                ID = product.ID.value,
                storageID = product.storageID.value,
                containerID = product.containerID?.value,
                name = product.name,
                description = product.description,
                bestBeforeDate = product.bestBefore,
                reminderDate = product.reminderDate,
                amount = product.amount,
                isDirty = true,
                isDeleted = false
            )
        })
    }

    override suspend fun insertStorage(storage: Storage) {
        dao.insertStorage(StorageEntity(
            ID = storage.ID.value,
            name = storage.name,
            isDirty = true,
            isDeleted = false
        ))
    }

    override suspend fun insertContainer(container: Container) {
        dao.insertContainer(ContainerEntity(
            ID = container.ID.value,
            storageID = container.storageID,
            name = container.name,
            isDirty = true,
            isDeleted = false
        ))
    }

    override suspend fun updateProduct(product: Product) {
        dao.updateProduct(ProductEntity(
            ID = product.ID.value,
            storageID = product.storageID.value,
            containerID = product.containerID?.value,
            name = product.name,
            description = product.description,
            bestBeforeDate = product.bestBefore,
            reminderDate = product.reminderDate,
            amount = product.amount,
            isDirty = true,
            isDeleted = false
        ))
    }

    override suspend fun updateStorage(storage: Storage) {
        dao.updateStorage(StorageEntity(
            ID = storage.ID.value,
            name = storage.name,
            isDirty = true,
            isDeleted = false
        ))
    }

    override suspend fun deleteProduct(product: Product) {
        dao.updateProduct(ProductEntity(
            ID = product.ID.value,
            storageID = product.storageID.value,
            containerID = product.containerID?.value,
            name = product.name,
            description = product.description,
            bestBeforeDate = product.bestBefore,
            reminderDate = product.reminderDate,
            amount = product.amount,
            isDirty = true,
            isDeleted = true
        ))
    }

    override fun getStoragesWithContainersShell(): Flow<Map<Storage, List<Container>>> {
        return dao.getStoragesWithContainersShell().map { entity ->
            entity.entries.associate { (storageEntity, containersEntity) ->
                val storage = storageEntity.toDomain()
                val containers = containersEntity.map { it.toDomain() }
                storage to containers
            }
        }
    }

    override fun getStorageWithProducts(storageID: String): Flow<StorageWithProducts?> {
        return dao.getStorageWithProducts(storageID).map { map ->
            map.entries.firstOrNull()?.let { (storageEntity, productEntities) ->
                StorageWithProducts(
                    storage = storageEntity.toDomain(),
                    products = productEntities.map { it.toDomain() }
                )
            }
        }
    }

    override fun getAllProductsOrderedByAlphabet(): Flow<List<Product>> {
        return dao.getAllProductsOrderedByAlphabet().map { products ->
            products.map { it.toDomain() }
        }
    }

    override fun getStorageContainersWithProducts(storageID: String, sortOrder: ContainerSortOrder): Flow<List<ContainerWithProducts>> {
        val containers = when(sortOrder) {
            ALPHABETICALLY -> dao.getStorageContainersWithProductsOrderedByName(storageID)
            BEST_BEFORE -> dao.getStorageContainersWithProductsOrderedByBB(storageID)
        }
        return containers.map { map ->
            map.map { (containerEntity, productEntities) ->
                ContainerWithProducts(
                    container = containerEntity.toDomain(),
                    products = productEntities.map { it.toDomain() }
                )
            }
        }
    }

    override fun getStorageProductsWithoutContainer(storageID: String, sortOrder: ContainerSortOrder): Flow<List<Product>> {
        val containers = when(sortOrder) {
            ALPHABETICALLY -> dao.getStorageProductsWithoutContainerOrderedByName(storageID)
            BEST_BEFORE -> dao.getStorageProductsWithoutContainerOrderedByBB(storageID)
        }
        return containers.map { products ->
            products.map { it.toDomain() }
        }
    }


    override fun getProductsOrderedByBB(): Flow<List<Product>> {
        return dao.getProductsOrderedByBB().map { products ->
            products.map { it.toDomain() }
        }
    }

    override fun getStorageName(storageID: String): Flow<String> {
        return dao.getStorageName(storageID)
    }

    override fun getAllProducts(): Flow<List<Product>> {
        return dao.getAllProducts().map { products ->
            products.map { it.toDomain() }
        }
    }
}

