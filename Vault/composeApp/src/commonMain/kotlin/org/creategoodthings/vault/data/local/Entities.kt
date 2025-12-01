package org.creategoodthings.vault.data.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import kotlinx.datetime.LocalDate

@Entity(
    tableName = "products",
    foreignKeys = [
        ForeignKey(
            entity = StorageEntity::class,
            parentColumns = ["ID"],
            childColumns = ["storageID"]
        ),
        ForeignKey(
            entity = ContainerEntity::class,
            parentColumns = ["ID"],
            childColumns = ["containerID"]
        )
    ],
    indices = [Index(value = ["name"]), Index(value = ["bestBeforeDate"]), Index(value = ["storageID"]), Index(value = ["containerID"])]
)
data class ProductEntity(
    @PrimaryKey val ID: String,
    val storageID: String,
    val containerID: String?,
    val name: String,
    val description: String,
    val bestBeforeDate: LocalDate,
    val reminderDate: LocalDate,
    val amount: Int,
    val isDirty: Boolean,
    val isDeleted: Boolean
)

@Entity(
    tableName = "storage"
)
data class StorageEntity(
    @PrimaryKey val ID: String,
    val name: String,
    val isDirty: Boolean,
    val isDeleted: Boolean
)

@Entity(
    tableName = "container",
    foreignKeys = [
        ForeignKey(
            entity = StorageEntity::class,
            parentColumns = ["ID"],
            childColumns = ["storageID"]
        ),
    ],
    indices = [Index(value = ["storageID"])]
)
data class ContainerEntity(
    @PrimaryKey val ID: String,
    val storageID: String,
    val name: String,
    val isDirty: Boolean,
    val isDeleted: Boolean
)

data class StorageWithProductsEntity(
    @Embedded val storage: StorageEntity,
    @Relation(
        parentColumn = "ID",
        entityColumn = "storageID"
    )
    val products: List<ProductEntity>
)