package org.creategoodthings.vault.domain

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.todayIn
import kotlin.jvm.JvmInline
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * The UI/Front facing Product
 */
@OptIn(ExperimentalUuidApi::class)
data class Product(
    val ID: ProductID,
    val name: String,
    val amount: Int,
    val description: String,
    val storageID: StorageID,
    val containerID: ContainerID? = null,
    val bestBefore: LocalDate,
    val reminderDate: LocalDate,
)

@JvmInline
value class ProductID(val value: String) {
    override fun toString(): String = value
    companion object {
        @OptIn(ExperimentalUuidApi::class)
        fun generate(): ProductID = ProductID(Uuid.random().toString())
    }
}

@OptIn(ExperimentalTime::class)
fun Product.calculateDaysRemaining(): Int {
    val timeZone = TimeZone.currentSystemDefault()
    val today = Clock.System.todayIn(timeZone)
    return today.daysUntil(bestBefore)
}