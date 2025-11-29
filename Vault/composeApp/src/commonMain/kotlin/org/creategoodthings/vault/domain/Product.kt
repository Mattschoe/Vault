package org.creategoodthings.vault.domain

import kotlinx.datetime.LocalDate

/**
 * The UI/Front facing Product
 */
data class Product(
    val name: String,
    val amount: Int,
    val description: String,
    val storageID: String,
    val containerID: String?,
    val expireDate: LocalDate,
    val bestBefore: LocalDate,
    val reminderDate: LocalDate,
    val daysRemaining: Int
)