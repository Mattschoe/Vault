package org.creategoodthings.vault.data.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.flow.first
import org.creategoodthings.vault.config.AppConfig
import org.creategoodthings.vault.data.local.ProductDao
import org.creategoodthings.vault.domain.InviteError
import org.creategoodthings.vault.domain.Result
import org.creategoodthings.vault.domain.Result.*
import org.creategoodthings.vault.domain.SyncError
import org.creategoodthings.vault.domain.repositories.PreferencesRepository
import org.creategoodthings.vault.domain.repositories.SyncRepository
import kotlin.time.Clock
import kotlin.time.ExperimentalTime


@OptIn(ExperimentalTime::class)
class PocketbaseSyncRepository(
    private val _productDao: ProductDao,
    private val _pocketBaseClient: HttpClient,
    private val _prefRepo: PreferencesRepository
): SyncRepository {
    override suspend fun sync(): Result<Unit, SyncError> {
        val userID = _prefRepo.userID.first() ?: return Error(SyncError("User not validated"))
        run {
            if (pushStorages(userID) is Error) return@run
            if (pushContainers() is Error) return@run
            if (pushProducts() is Error) return@run
        }
        val lastSync = _prefRepo.lastSync.first()?.toString() ?: ""
        return when (val pullResult = pullChanges(lastSync.toString())) {
            is Error -> Error(pullResult.error)
            is Success -> {
                _prefRepo.setLastSync(Clock.System.now())
                Success(Unit)
            }
        }
    }

    private suspend fun pullChanges(lastSync: String?): Result<Unit, SyncError> {
        return try {
            val filter = if (lastSync != null) "updated >= '$lastSync'" else ""
            val remoteStorages = _pocketBaseClient.get(AppConfig.STORAGES_ENDPOINT) {
                parameter("filter", filter)
            }.body<PocketBaseResponse<StorageDTO>>().items

            val remoteContainers = _pocketBaseClient.get(AppConfig.CONTAINERS_ENDPOINT) {
                parameter("filter", filter)
            }.body<PocketBaseResponse<ContainerDTO>>().items

            val remoteProducts = _pocketBaseClient.get(AppConfig.PRODUCTS_ENDPOINT) {
                parameter("filter", filter)
            }.body<PocketBaseResponse<ProductDTO>>().items

            _productDao.syncStorages(remoteStorages.map { it.toEntity(false) })
            _productDao.syncContainers(remoteContainers.map { it.toEntity(false) })
            _productDao.syncProducts(remoteProducts.map { it.toEntity(false) })
            Success(Unit)
        } catch (e: Exception) {
            Error(SyncError(e.message ?: "Error pulling changes"))
        }
    }

    private suspend fun pushProducts(): Result<Unit, SyncError> {
        val dirtyProducts = _productDao.getAllDirtyProducts()

        dirtyProducts.forEach { entity ->
            try {
                _pocketBaseClient.patch("${AppConfig.PRODUCTS_ENDPOINT}/${entity.ID}") {
                    contentType(ContentType.Application.Json)
                    setBody(entity.toDTO())
                }
                _productDao.markProductAsClean(entity.ID)
            } catch (e: ClientRequestException) {
                if (e.response.status == HttpStatusCode.NotFound) {
                    //Not created, try to create
                    try {
                        _pocketBaseClient.post(AppConfig.PRODUCTS_ENDPOINT) {
                            contentType(ContentType.Application.Json)
                            setBody(entity.toDTO())
                        }
                        _productDao.markProductAsClean(entity.ID)
                    } catch (e: Exception) {
                        return Error(SyncError(e.message ?: "Unable to sync product: $entity"))
                    }
                } else {
                    return Error(SyncError("Error updating product: $entity"))
                }
            } catch (e: Exception) {
                return Error(SyncError(e.message ?: "Unknow error trying to sync product: $entity"))
            }
        }
        return Success(Unit)
    }

    private suspend fun pushStorages(currentUserID: String): Result<Unit, SyncError> {
        val dirtyStorages = _productDao.getAllDirtyStorages()

        dirtyStorages.forEach { entity ->
            val dto = entity.toDTO()
            try {
                _pocketBaseClient.patch("${AppConfig.STORAGES_ENDPOINT}/${entity.ID}") {
                    contentType(ContentType.Application.Json)
                    setBody(dto)
                }
                _productDao.markStorageAsClean(entity.ID)
            } catch (e: ClientRequestException) {
                if (e.response.status == HttpStatusCode.NotFound) {
                    try {
                        _pocketBaseClient.post(AppConfig.STORAGES_ENDPOINT) {
                            contentType(ContentType.Application.Json)
                            setBody(dto.copy(users = listOf(currentUserID)))
                        }
                        _productDao.markStorageAsClean(entity.ID)
                    } catch (e: Exception) {
                        return Error(SyncError(e.message ?: "Unable to sync product: $entity"))
                    }
                } else {
                    return Error(SyncError(e.message))
                }
            } catch (e: Exception) {
                return Error(SyncError(e.message ?: "Unknow error trying to sync product: $entity"))
            }
        }
        return Success(Unit)
    }

    private suspend fun pushContainers(): Result<Unit, SyncError> {
        val dirtyContainers = _productDao.getAllDirtyContainers()

        dirtyContainers.forEach { entity ->
            try {
                _pocketBaseClient.patch("${AppConfig.CONTAINERS_ENDPOINT}/${entity.ID}") {
                    contentType(ContentType.Application.Json)
                    setBody(entity.toDTO())
                }
                _productDao.markContainerAsClean(entity.ID)
            } catch (e: ClientRequestException) {
                if (e.response.status == HttpStatusCode.NotFound) {
                    try {
                        _pocketBaseClient.post(AppConfig.CONTAINERS_ENDPOINT) {
                            contentType(ContentType.Application.Json)
                            setBody(entity.toDTO())
                        }
                         _productDao.markContainerAsClean(entity.ID)
                    } catch (e: Exception) {
                        return Error(SyncError(e.message ?: "Unable to sync product: $entity"))
                    }
                } else {
                    return Error(SyncError(e.message))
                }
            } catch (e: Exception) {
                return Error(SyncError(e.message ?: "Unable to sync product: $entity"))
            }
        }
        return Success(Unit)
    }
}