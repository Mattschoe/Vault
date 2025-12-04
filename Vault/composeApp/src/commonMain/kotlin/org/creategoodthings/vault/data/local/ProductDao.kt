package org.creategoodthings.vault.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    //region INSERTS
    @Insert
    suspend fun insertProduct(product: ProductEntity)

    @Insert
    suspend fun insertStorage(storage: StorageEntity)

    @Insert
    suspend fun insertContainer(container: ContainerEntity)

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Update
    suspend fun updateStorage(storage: StorageEntity)
    //endregion

    //region DELETE


    //endregion

    //region QUERIES
    @Query("SELECT name FROM storage WHERE ID = :storageID")
    fun getStorageName(storageID: String): Flow<String>

    @Query("SELECT * FROM storage LEFT JOIN container ON storage.ID = container.storageID")
    fun getStoragesWithContainersShell(): Flow<Map<StorageEntity, List<ContainerEntity>>>

    @Transaction
    @Query("SELECT * FROM storage WHERE ID = :storageID")
    fun getStorageWithProducts(storageID: String): Flow<StorageWithProductsEntity?>

    @Query("SELECT * FROM products ORDER BY name")
    fun getAllProductsOrderedByAlphabet(): Flow<List<ProductEntity>>

    @Transaction
    @Query("SELECT * FROM container WHERE storageID = :storageID")
    fun getStorageContainersWithProducts(storageID: String): Flow<List<ContainerWithProductsEntity>>

    @Query("SELECT * FROM products WHERE containerID IS NULL AND storageID = :storageID ORDER BY bestBeforeDate")
    fun getStorageProductsWithoutContainerOrderedByBB(storageID: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products ORDER BY bestBeforeDate")
    fun getProductsOrderedByBB(): Flow<List<ProductEntity>>
    //endregion
}