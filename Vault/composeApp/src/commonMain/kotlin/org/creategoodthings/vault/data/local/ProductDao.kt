package org.creategoodthings.vault.data.local

import androidx.room.Dao
import androidx.room.Delete
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

    //region DELETIONG
    @Query("UPDATE products SET isDeleted = true, isDirty = true WHERE ID = :productID")
    suspend fun deleteProduct(productID: String)
    //endregion

    //region QUERIES
    @Query("SELECT name FROM storage WHERE ID = :storageID AND isDeleted = false")
    fun getStorageName(storageID: String): Flow<String>

    @Query("SELECT * FROM storage LEFT JOIN container ON storage.ID = container.storageID AND container.isDeleted = false WHERE storage.isDeleted = false")
    fun getStoragesWithContainersShell(): Flow<Map<StorageEntity, List<ContainerEntity>>>

    @Query("""
        SELECT * FROM storage 
        LEFT JOIN products ON storage.ID = products.storageID AND products.isDeleted = false
        WHERE storage.ID = :storageID AND storage.isDeleted = false
    """)
    fun getStorageWithProducts(storageID: String): Flow<Map<StorageEntity, List<ProductEntity>>>

    @Query("SELECT * FROM products WHERE isDeleted = false ORDER BY name")
    fun getAllProductsOrderedByAlphabet(): Flow<List<ProductEntity>>

    @Query("""
        SELECT * FROM container 
        LEFT JOIN products ON container.ID = products.containerID AND products.isDeleted = false
        WHERE container.storageID = :storageID AND container.isDeleted = false
    """)
    fun getStorageContainersWithProducts(storageID: String): Flow<Map<ContainerEntity, List<ProductEntity>>>

    @Query("SELECT * FROM products WHERE containerID IS NULL AND storageID = :storageID AND isDeleted = false ORDER BY bestBeforeDate")
    fun getStorageProductsWithoutContainerOrderedByBB(storageID: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE isDeleted = false ORDER BY bestBeforeDate")
    fun getProductsOrderedByBB(): Flow<List<ProductEntity>>
    //endregion
}