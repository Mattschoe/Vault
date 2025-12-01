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
import org.creategoodthings.vault.domain.repositories.ProductRepository
import org.creategoodthings.vault.domain.repositories.StorageWithProducts
import org.creategoodthings.vault.domain.repositories.toDomain

class OfflineProductRepository(private val dao: ProductDao): ProductRepository {
    override suspend fun insertProduct(product: Product) {
        dao.insertProduct(ProductEntity(
            ID = product.ID,
            storageID = product.storageID,
            containerID = product.containerID,
            name = product.name,
            description = product.description,
            bestBeforeDate = product.bestBefore,
            reminderDate = product.reminderDate,
            amount = product.amount,
            isDirty = true,
            isDeleted = false
        ))
    }

    override suspend fun insertStorage(storage: Storage) {
        dao.insertStorage(StorageEntity(
            ID = storage.ID,
            name = storage.name,
            isDirty = true,
            isDeleted = false
        ))
    }

    override suspend fun insertContainer(container: Container) {
        dao.insertContainer(ContainerEntity(
            ID = container.ID,
            storageID = container.storageID,
            name = container.name,
            isDirty = true,
            isDeleted = false
        ))
    }

    override fun getStoragesWithContainersShell(): Flow<Map<Storage, List<Container>>> {
        return dao.getStoragesWithContainersShell().map { entity ->
            entity.map { (storageEntity, containersEntity) ->
                val storage = Storage(
                    ID = storageEntity.ID,
                    name = storageEntity.name
                )
                val containers = containersEntity.map {
                    Container(
                        ID = it.ID,
                        storageID = it.storageID,
                        name = it.name
                    )
                }
                storage to containers
            }.toMap()
        }
    }

    override fun getStorageWithProducts(storageID: String): Flow<StorageWithProducts> {
        return dao.getStorageWithProducts(storageID).map { entity ->
            StorageWithProducts(
                storage = Storage(
                    ID = entity.storage.ID,
                    name = entity.storage.name
                ),
                products = entity.products.map { it.toDomain() }
            )
        }
    }

    override fun getAllProductsOrderedByAlphabet(): Flow<List<Product>> {
        return dao.getAllProductsOrderedByAlphabet().map { products ->
            products.map { it.toDomain() }
        }
    }

    override fun getContainersWithProductsOrderedByBB(): Flow<Map<Container, List<Product>>> {
        return dao.getContainersWithProductsOrderedByBB().map { entity ->
            entity.map { (containerEntity, productsEntity) ->
                val container = Container(
                    ID = containerEntity.ID,
                    storageID = containerEntity.storageID,
                    name = containerEntity.name
                )
                val products = productsEntity.map { it.toDomain() }
                container to products
            }.toMap()
        }
    }

    override fun getStoragesWithProductsOrderedByBB(): Flow<Map<Storage, List<Product>>> {
        return dao.getStoragesWithProductsOrderedByBB().map { entity ->
            entity.map { (storageEntity, productsEntity) ->
                val storage = Storage(
                    ID = storageEntity.ID,
                    name = storageEntity.name
                )
                val products = productsEntity.map { it.toDomain() }
                storage to products
            }.toMap()
        }
    }

    override fun getProductsOrderedByBB(): Flow<List<Product>> {
        return dao.getProductsOrderedByBB().map { products ->
            products.map { it.toDomain() }
        }
    }
}

