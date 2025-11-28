package org.creategoodthings.vault.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDate

@Entity(
    tableName = "products",
    foreignKeys = [
        ForeignKey(
            entity = Storage::class,
            parentColumns = ["ID"],
            childColumns = ["storageID"]
        )
    ],
    indices = [Index(value = ["name"]), Index(value = ["bestBeforeDate"]), Index(value = ["storageID"])]
)
data class ProductEntity(
    @PrimaryKey val ID: String,
    val storageID: String,
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
data class Storage(
    @PrimaryKey val ID: String,
    val name: String,
    val isDirty: Boolean,
    val isDeleted: Boolean
)