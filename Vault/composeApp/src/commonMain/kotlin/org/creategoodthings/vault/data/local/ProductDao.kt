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
    //endregion

    //region DELETE


    //endregion

    //region QUERIES
    @Transaction
    @Query("SELECT * FROM storage WHERE ID = :storageID")
    fun getStorageWithProducts(storageID: String): Flow<StorageWithProductsEntity>

    @Query("SELECT * FROM products ORDER BY name")
    fun getAllProductsOrderedByAlphabet(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM container JOIN products ON container.ID = products.containerID ORDER BY bestBeforeDate")
    fun getContainersWithProductsOrderedByBB(): Flow<Map<ContainerEntity, List<ProductEntity>>>

    @Query("SELECT * FROM storage JOIN products ON storage.ID = products.storageID ORDER BY bestBeforeDate")
    fun getStoragesWithProductsOrderedByBB(): Flow<Map<StorageEntity, List<ProductEntity>>>

    @Query("SELECT * FROM products ORDER BY bestBeforeDate")
    fun getProductsOrderedByBB(): Flow<List<ProductEntity>>
    //endregion
}