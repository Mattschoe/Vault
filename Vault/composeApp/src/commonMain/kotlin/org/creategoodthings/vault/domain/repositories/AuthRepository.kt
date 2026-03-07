package org.creategoodthings.vault.domain.repositories

import kotlinx.coroutines.flow.Flow
import org.creategoodthings.vault.domain.InviteError
import org.creategoodthings.vault.domain.NetworkError
import org.creategoodthings.vault.domain.Result
import org.creategoodthings.vault.domain.User


interface AuthRepository {
    val currentUser: Flow<User?>

    /**
     * Initializes the auth repository by trying to log the user in with a (potential) saved token.
     */
    suspend fun initialize(): Result<Unit, NetworkError>
    suspend fun login(email: String, password: String): Result<Unit, NetworkError>
    suspend fun register(username: String, email: String, password: String): Result<Unit, NetworkError>
    suspend fun inviteUserToStorage(storageID: String, userEmailToInvite: String): Result<Unit, InviteError>
    suspend fun logout()
    suspend fun refreshUser(): Result<Unit, NetworkError>
}