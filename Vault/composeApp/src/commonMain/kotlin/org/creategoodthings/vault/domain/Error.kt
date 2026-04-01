package org.creategoodthings.vault.domain

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import vault.composeapp.generated.resources.Res
import vault.composeapp.generated.resources.log_in_error
import vault.composeapp.generated.resources.network_error
import vault.composeapp.generated.resources.email_already_registered
import vault.composeapp.generated.resources.register_error
import vault.composeapp.generated.resources.server_offline

sealed interface Error

data class PurchaseError(val message: String): Error

enum class NetworkError : Error {
    TOKEN_IS_NULL_OR_BLANK,
    SERVER_REJECTED_TOKEN,
    SERVER_OFFLINE,
    UNEXPECTED_INIT_ERROR,
    NETWORK_UNAVAILABLE,
    LOG_IN_ERROR,
    REGISTER_ERROR,
    EMAIL_ALREADY_REGISTERED,
    USER_REFRESH_ERROR;

    companion object {
        /**
         * Transforms the user-relevant network errors into user-readable
         * error messages. If the error is not understandable/relevant for
         * the user this function instead returns a generic "Network error"
         * message.
         */
        fun NetworkError.getResource(): StringResource {
            return when (this) {
                LOG_IN_ERROR -> Res.string.log_in_error
                REGISTER_ERROR -> Res.string.register_error
                EMAIL_ALREADY_REGISTERED -> Res.string.email_already_registered
                SERVER_OFFLINE -> Res.string.server_offline
                NETWORK_UNAVAILABLE, USER_REFRESH_ERROR, SERVER_REJECTED_TOKEN,
                TOKEN_IS_NULL_OR_BLANK, SERVER_REJECTED_TOKEN, UNEXPECTED_INIT_ERROR -> Res.string.network_error
            }
        }
    }
}

data class SyncError(val message: String): Error

enum class InviteError : Error {
    USER_DOESNT_EXISTS
}