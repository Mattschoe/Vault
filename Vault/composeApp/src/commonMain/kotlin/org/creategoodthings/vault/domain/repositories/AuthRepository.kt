package org.creategoodthings.vault.domain.repositories

import kotlinx.coroutines.flow.Flow
import org.creategoodthings.vault.domain.User

interface AuthRepository {
    val currentUser: Flow<User?>
    suspend fun login(email: String, password: String): Result<Unit>
    suspend fun register(username: String, email: String, password: String): Result<Unit>
    suspend fun logout()
    suspend fun refreshUser(): Result<Unit>
}