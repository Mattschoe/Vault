package org.creategoodthings.vault.data.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.creategoodthings.vault.domain.repositories.PreferencesRepository
import org.creategoodthings.vault.ui.pages.storage.SortOption

class OfflinePreferencesRepository(
    private val dataStore: DataStore<Preferences>
): PreferencesRepository {
    private object Keys {
        val STANDARD_STORAGE_ID = stringPreferencesKey("standard_storage_id")
        val SORT_OPTION = stringPreferencesKey("sort_option")
    }

    override val standardStorageID = dataStore.data.map { preferences ->
        preferences[Keys.STANDARD_STORAGE_ID]
    }

    override val sortOption = dataStore.data.map { prefs ->
        val name = prefs[Keys.SORT_OPTION]
        if (name == null) null
        else SortOption.valueOf(name)
    }

    override suspend fun setStandardStorageID(storageID: String) {
        dataStore.edit { preferences ->
            preferences[Keys.STANDARD_STORAGE_ID] = storageID
        }
    }

    override suspend fun setSortOption(sortOption: SortOption) {
        dataStore.edit { prefs ->
            prefs[Keys.SORT_OPTION] = sortOption.name
        }
    }
}