package org.creategoodthings.vault.data.repositories

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.headers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import org.creategoodthings.vault.data.network.AuthResponseDTO
import org.creategoodthings.vault.data.network.LoginRequestDTO
import org.creategoodthings.vault.data.network.RegisterRequestDTO
import org.creategoodthings.vault.domain.User
import org.creategoodthings.vault.domain.repositories.AuthRepository
import org.creategoodthings.vault.domain.repositories.PreferencesRepository

class KtorAuthRepository(
    private val _client: HttpClient,
    private val _prefRepo: PreferencesRepository
) : AuthRepository {
    private val _currentUser = MutableStateFlow<User?>(null)
    override val currentUser = _currentUser.asStateFlow()

    /**
     * Checks on app start if we already have a token
     */
    suspend fun initialize() {
        val savedToken = _prefRepo.token.first() ?: return
        try {
            val response = _client.post("api/collections/users/auth-refresh") {
                headers { append(HttpHeaders.Authorization, savedToken) }
            }.body<AuthResponseDTO>()
            _currentUser.value = response.toDomain()
            _prefRepo.setToken(response.token)
        } catch (e: Exception) {
            _prefRepo.clearToken()
            _currentUser.value = null
            println("Token expired or invalid: ${e.message}") //TODO BETTER LOGGING
        }
    }

    override suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            val response = _client.post("/api/collections/users/auth-with-password") {
                contentType(ContentType.Application.Json)
                setBody(LoginRequestDTO(identity = email, password = password))
            }.body<AuthResponseDTO>()
            _prefRepo.setToken(response.token)
            _currentUser.value = response.toDomain()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(username: String, email: String, password: String): Result<Unit> {
        return try {
            _client.post("/api/collections/users/records") {
                contentType(ContentType.Application.Json)
                setBody(
                    RegisterRequestDTO(
                        username = username,
                        email = email,
                        password = password,
                        passwordConfirm = password
                    )
                )
            }
            login(email, password) //Auto login for better UX
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        _prefRepo.clearToken()
        _currentUser.value = null
    }
}

fun AuthResponseDTO.toDomain(): User {
    return User(
        ID = this.record.ID,
        email = this.record.email,
        token = this.token,
        isPremium = this.record.isPremium
    )
}