package org.creategoodthings.vault.domain

sealed interface Error

data class PurchaseError(val message: String): Error

data class NetworkError(val message: String): Error

data class SyncError(val message: String): Error

enum class InviteError : Error {
    USER_DOESNT_EXISTS
}