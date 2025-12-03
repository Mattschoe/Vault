package org.creategoodthings.vault.domain.repositories

import kotlinx.coroutines.flow.Flow
import org.creategoodthings.vault.ui.pages.storage.SortOption

interface PreferencesRepository {
    val standardStorageID: Flow<String?>
    val sortOption: Flow<SortOption?>
    suspend fun setStandardStorageID(storageID: String)
    suspend fun setSortOption(sortOption: SortOption)
}