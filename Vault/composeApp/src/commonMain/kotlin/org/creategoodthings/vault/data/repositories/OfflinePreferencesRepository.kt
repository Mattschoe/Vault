package org.creategoodthings.vault.data.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.map
import org.creategoodthings.vault.domain.repositories.PreferencesRepository

class OfflinePreferencesRepository(
    private val dataStore: DataStore<Preferences>
): PreferencesRepository {
    private object Keys {
        val STANDARD_STORAGE_ID = stringPreferencesKey("standard_storage_id")
    }

    override val standardStorageID = dataStore.data.map { preferences ->
        preferences[Keys.STANDARD_STORAGE_ID]
    }

    override suspend fun setStandardStorageID(storageID: String) {
        dataStore.edit { preferences ->
            preferences[Keys.STANDARD_STORAGE_ID] = storageID
        }
    }
}