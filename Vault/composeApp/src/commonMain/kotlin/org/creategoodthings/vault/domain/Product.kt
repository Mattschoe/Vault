package org.creategoodthings.vault.domain

import kotlinx.datetime.LocalDate

/**
 * The UI/Front facing Product
 */
data class Product(
    val name: String,
    val expireDate: LocalDate,
    val daysRemaining: Int,
    val bestBefore: LocalDate
)