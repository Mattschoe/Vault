package org.creategoodthings.vault.domain

import kotlinx.datetime.LocalDate
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * The UI/Front facing Product
 */
data class Product @OptIn(ExperimentalUuidApi::class) constructor(
    val ID: String = Uuid.random().toString(),
    val name: String,
    val amount: Int,
    val description: String,
    val storageID: String,
    val containerID: String?,
    val bestBefore: LocalDate,
    val reminderDate: LocalDate,
    val daysRemaining: Int
)