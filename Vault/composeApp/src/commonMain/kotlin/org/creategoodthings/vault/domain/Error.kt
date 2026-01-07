package org.creategoodthings.vault.domain

sealed interface Error

data class PurchaseError(val message: String): Error