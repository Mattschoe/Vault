package org.creategoodthings.vault.data.local

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import org.creategoodthings.vault.data.repositories.Converters

@Database(
    entities = [ProductEntity::class, StorageEntity::class, ContainerEntity::class],
    version = 3
)
@TypeConverters(Converters::class)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao() : ProductDao
}


@Suppress("KotlinNoActualForExpect") // The Room compiler generates the `actual` implementations.
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}