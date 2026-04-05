package org.creategoodthings.vault.data.repositories

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.statement.bodyAsText
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.headers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import okio.IOException
import org.creategoodthings.vault.config.AppConfig
import org.creategoodthings.vault.data.network.AuthResponseDTO
import org.creategoodthings.vault.data.network.InviteRequestDTO
import org.creategoodthings.vault.data.network.LoginRequestDTO
import org.creategoodthings.vault.data.network.RegisterRequestDTO
import org.creategoodthings.vault.domain.InviteError
import org.creategoodthings.vault.domain.NetworkError
import org.creategoodthings.vault.domain.Result
import org.creategoodthings.vault.domain.Result.Error
import org.creategoodthings.vault.domain.Result.Success
import org.creategoodthings.vault.domain.StorageID
import org.creategoodthings.vault.domain.User
import org.creategoodthings.vault.domain.repositories.AuthRepository
import org.creategoodthings.vault.domain.repositories.PreferencesRepository
import org.creategoodthings.vault.domain.services.PurchaseManager
import org.creategoodthings.vault.ui.pages.premium.LoginStateError


class KtorAuthRepository(
    private val _client: HttpClient,
    private val _prefRepo: PreferencesRepository,
    private val _purchaseManager: PurchaseManager
) : AuthRepository {
    private val _currentUser = MutableStateFlow<User?>(null)
    override val currentUser = _currentUser.asStateFlow()


    override suspend fun initialize(): Result<Unit, NetworkError> {
        try {
            val savedToken = _prefRepo.token.first()
            if (savedToken.isNullOrBlank()) {
                return Error(NetworkError.TOKEN_IS_NULL_OR_BLANK)
            }

            val response = _client.post(AppConfig.AUTH_REFRESH_ENDPOINT) {
                bearerAuth(savedToken)
            }

            if (response.status.value in 200..299) {
                val data = response.body<AuthResponseDTO>()
                _prefRepo.setToken(data.token)
                _prefRepo.setUserID(data.record.ID)
                _currentUser.value = data.toDomain()
                _purchaseManager.logIn(data.record.ID)
            } else {
                return Error(NetworkError.SERVER_REJECTED_TOKEN)
            }
        } catch (e: ConnectTimeoutException) {
            _currentUser.value = null
            return Error(NetworkError.NETWORK_UNAVAILABLE)
        } catch (e: Exception) {
            _prefRepo.clearToken()
            _prefRepo.clearUserID()
            _currentUser.value = null
            return Error(NetworkError.UNEXPECTED_INIT_ERROR)
        }
        return Success(Unit)
    }

    override suspend fun login(email: String, password: String): Result<Unit, NetworkError> {
        return try {
            val response = _client.post(AppConfig.AUTH_WITH_PASSWORD_ENDPOINT) {
                contentType(ContentType.Application.Json)
                setBody(LoginRequestDTO(identity = email, password = password))
            }.body<AuthResponseDTO>()
            _prefRepo.setToken(response.token)
            _prefRepo.setUserID(response.record.ID)
            _currentUser.value = response.toDomain()
            _purchaseManager.logIn(_currentUser.value!!.ID)
            Success(Unit)
        } catch (_: IOException) {
            Error(NetworkError.SERVER_OFFLINE)
        } catch (e: ClientRequestException) {
          val body = e.response.bodyAsText()
          if ("Failed to authenticate" in body) Error(NetworkError.NO_USER_FOUND)
          else Error(NetworkError.LOG_IN_ERROR)
        } catch (_: Exception) {
            Error(NetworkError.LOG_IN_ERROR)
        }
    }

    override suspend fun register(username: String, email: String, password: String): Result<Unit, NetworkError>  {
        return try {
            _client.post(AppConfig.USERS_ENDPOINT) {
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
            Success(Unit)
        } catch (e: ClientRequestException) {
            val body = e.response.bodyAsText()
            if ("validation_not_unique" in body) {
                Error(NetworkError.EMAIL_ALREADY_REGISTERED)
            } else {
                Error(NetworkError.REGISTER_ERROR)
            }
        } catch (_: IOException) {
            Error(NetworkError.SERVER_OFFLINE)
        } catch (e: Exception) {
            Error(NetworkError.REGISTER_ERROR)
        }
    }

    override suspend fun logout() {
        _purchaseManager.logOut()
        _prefRepo.clearToken()
        _prefRepo.clearUserID()
        _currentUser.value = null
    }

    override suspend fun refreshUser(): Result<Unit, NetworkError> {
        return try {
            val currentToken = _prefRepo.token.first() ?: return Error(NetworkError.TOKEN_IS_NULL_OR_BLANK)
            val response = _client.post(AppConfig.AUTH_REFRESH_ENDPOINT) {
                headers { bearerAuth(currentToken) }
            }.body<AuthResponseDTO>()
            _currentUser.value = response.toDomain()
            Success(Unit)
        } catch (e: Exception) {
            Error(NetworkError.USER_REFRESH_ERROR)
        }
    }

    override suspend fun inviteUserToStorage(storageID: StorageID, userEmailToInvite: String): Result<Unit, InviteError> {
        return try {
            _client.post(AppConfig.INVITATIONS_ENDPOINT) {
                contentType(ContentType.Application.Json)
                setBody(InviteRequestDTO(
                    email = userEmailToInvite,
                    storageID = storageID.value
                ))
            }
            Success(Unit)
        } catch (_: Exception) {
            Error(InviteError.USER_DOESNT_EXISTS)
        }
    }
}

fun AuthResponseDTO.toDomain(): User {
    return User(
        ID = record.ID,
        email = record.email,
        token = token,
        isPremium = record.isPremium,
        username = record.username
    )
}