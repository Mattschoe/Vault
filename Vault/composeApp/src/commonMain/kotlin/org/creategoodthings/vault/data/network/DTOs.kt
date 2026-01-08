package org.creategoodthings.vault.data.network

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.creategoodthings.vault.data.local.ContainerEntity
import org.creategoodthings.vault.data.local.ProductEntity
import org.creategoodthings.vault.data.local.StorageEntity
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
data class AuthResponseDTO(
    @SerialName("token") val token: String,
    @SerialName("record") val record: UserDTO
)

@Serializable
data class LoginRequestDTO(
    @SerialName("identity") val identity: String,
    @SerialName("password") val password: String
)

@Serializable
data class RegisterRequestDTO(
    @SerialName("email") val email: String,
    @SerialName("password") val password: String,
    @SerialName("passwordConfirm") val passwordConfirm: String,
    @SerialName("name") val username: String
)

@Serializable
data class UserDTO(
    @SerialName("id") val ID: String,
    @SerialName("email") val email: String,
    @SerialName("is_premium") val isPremium: Boolean = false,
    @SerialName("username") val username: String? = null,
    @SerialName("created") val created: String? = null
)

@Serializable
data class ProductDTO(
    @SerialName("id") val ID: String,
    @SerialName("storage_id") val storageID: String,
    @SerialName("container_id") val containerID: String?,
    @SerialName("name") val name: String,
    @SerialName("description") val description: String,
    @SerialName("best_before") val bestBeforeDate: String,
    @SerialName("reminder_date") val reminderDate: String,
    @SerialName("amount") val amount: Int,
    @SerialName("is_deleted") val isDeleted: Boolean,
)

@Serializable
data class StorageDTO(
    @SerialName("id") val ID: String,
    @SerialName("name") val name: String,
    @SerialName("is_deleted") val isDeleted: Boolean,
    @SerialName("users") val users: List<String>? = null
)

@Serializable
data class ContainerDTO(
    @SerialName("id") val ID: String,
    @SerialName("storage_id") val storageID: String,
    @SerialName("name") val name: String,
    @SerialName("is_deleted") val isDeleted: Boolean,
)

fun ProductEntity.toDTO(): ProductDTO {
    return ProductDTO(
        ID = this.ID,
        storageID = this.storageID,
        containerID = this.containerID,
        name = this.name,
        description = this.description,
        bestBeforeDate = this.bestBeforeDate.toString() + " 00:00:00.000Z",
        reminderDate = this.reminderDate.toString() + " 00:00:00.000Z",
        amount = this.amount,
        isDeleted = this.isDeleted,
    )
}

fun StorageEntity.toDTO(): StorageDTO {
    return StorageDTO(
        ID = this.ID,
        name = this.name,
        isDeleted = this.isDeleted
    )
}

fun ContainerEntity.toDTO(): ContainerDTO {
    return ContainerDTO(
        ID = this.ID,
        storageID = this.storageID,
        name = this.name,
        isDeleted = this.isDeleted
    )
}

@OptIn(ExperimentalTime::class)
fun ProductDTO.toEntity(isDirty: Boolean): ProductEntity {
    val timeZone = TimeZone.currentSystemDefault()
    return ProductEntity(
        ID = this.ID,
        storageID = this.storageID,
        containerID = this.containerID,
        name = this.name,
        description = this.description,
        bestBeforeDate = this.bestBeforeDate.toInstant().toLocalDateTime(timeZone).date,
        reminderDate = this.reminderDate.toInstant().toLocalDateTime(timeZone).date,
        amount = this.amount,
        isDirty = isDirty,
        isDeleted = this.isDeleted
    )
}

fun StorageDTO.toEntity(isDirty: Boolean): StorageEntity {
    return StorageEntity(
        ID = this.ID,
        name = this.name,
        isDirty = isDirty,
        isDeleted = this.isDeleted
    )
}

fun ContainerDTO.toEntity(isDirty: Boolean): ContainerEntity {
    return ContainerEntity(
        ID = this.ID,
        storageID = this.storageID,
        name = this.name,
        isDirty = isDirty,
        isDeleted = this.isDeleted
    )
}

@OptIn(ExperimentalTime::class)
fun String.toInstant(): Instant {
    val isoString = this.replace(" ", "T")
    return Instant.parse(isoString)
}