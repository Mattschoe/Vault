package org.creategoodthings.vault.domain.services

import kotlinx.coroutines.flow.StateFlow
import org.creategoodthings.vault.domain.PurchaseError
import org.creategoodthings.vault.domain.Result

interface PurchaseManager {
    val isPremium: StateFlow<Boolean?>
    suspend fun getSubscriptionOptions(): Result<List<SubscriptionOption>, PurchaseError>
    suspend fun purchase(option: SubscriptionOption): Result<Unit, PurchaseError>
    suspend fun logIn(userID: String)
    suspend fun logOut()
}

data class SubscriptionOption(
    val ID: String,
    val title: String,
    val fullPrice: String,
    val pricePerMonth: String
)