package org.creategoodthings.vault.data.repositories

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.headers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.creategoodthings.vault.data.network.AuthResponseDTO
import org.creategoodthings.vault.data.network.LoginRequestDTO
import org.creategoodthings.vault.data.network.RegisterRequestDTO
import org.creategoodthings.vault.domain.User
import org.creategoodthings.vault.domain.repositories.AuthRepository
import org.creategoodthings.vault.domain.repositories.PreferencesRepository
import org.creategoodthings.vault.domain.services.PurchaseManager

class KtorAuthRepository(
    private val _client: HttpClient,
    private val _prefRepo: PreferencesRepository,
    private val _purchaseManager: PurchaseManager
) : AuthRepository {
    private val _currentUser = MutableStateFlow<User?>(null)
    override val currentUser = _currentUser.asStateFlow()

    private val _repoScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    init {
        _repoScope.launch {
            initialize()
        }
    }

    /**
     * Checks on app start if we already have a token
     */
    @Throws(Exception::class)
    suspend fun initialize() {
        try {
            val savedToken = _prefRepo.token.first()
            if (savedToken.isNullOrBlank()) {
                println("token is null or blank")
                _currentUser.value = null
                return
            }

            val response = _client.post("/api/collections/users/auth-refresh") {
                bearerAuth(savedToken)
            }

            if (response.status.value in 200..299) {
                val data = response.body<AuthResponseDTO>()
                _prefRepo.setToken(data.token)
                _currentUser.value = data.toDomain()
            } else {
                println("Server rejected token: ${response.status}")
                throw ClientRequestException(response, "Token invalid")
            }

        } catch (e: ClientRequestException) {
            println("Token expired or rejected. Clearing session.")
            _prefRepo.clearToken()
            _currentUser.value = null
        } catch (e: ConnectTimeoutException) {
            // Network is down. Fails silently for now. The user will see the login screen.
            println("Network unavailable. Keeping token.")
            _currentUser.value = null
        } catch (e: Exception) {
            println("Unexpected initialization error: ${e.message}")
            e.printStackTrace()
            _currentUser.value = null
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
            _purchaseManager.logIn(_currentUser.value!!.ID)
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
        _purchaseManager.logOut()
        _prefRepo.clearToken()
        _currentUser.value = null
    }

    override suspend fun refreshUser(): Result<Unit> {
        return try {
            val currentToken = _prefRepo.token.first() ?: return Result.failure(Exception("No token"))
            val response = _client.post("api/collections/users/auth-refresh") {
                headers { append(HttpHeaders.Authorization, currentToken) }
            }.body<AuthResponseDTO>()
            _currentUser.value = response.toDomain()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
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