package org.creategoodthings.vault.domain.repositories

import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    val standardStorageID: Flow<String?>
    suspend fun setStandardStorageID(storageID: String)
}