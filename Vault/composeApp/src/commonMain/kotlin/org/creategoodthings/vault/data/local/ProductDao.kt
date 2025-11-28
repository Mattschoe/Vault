package org.creategoodthings.vault.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    //region INSERTS
    @Insert
    suspend fun insertProduct(product: ProductEntity)

    @Update
    suspend fun updateProduct(product: ProductEntity)
    //endregion

    //region DELETE


    //endregion

    //region QUERIES
    @Query("SELECT * FROM products")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM storage JOIN products ON storage.ID = products.storageID")
    fun getStoragesWithProducts(): Flow<Map<StorageEntity, List<ProductEntity>>>

    @Query("SELECT * FROM products ORDER BY bestBeforeDate")
    fun getProductsOrderedByBB(): Flow<List<ProductEntity>>
    //endregion
}