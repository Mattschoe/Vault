package org.creategoodthings.vault.data.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalTime
import org.creategoodthings.vault.domain.repositories.ContainerSortOrder
import org.creategoodthings.vault.domain.repositories.PreferencesRepository
import org.creategoodthings.vault.ui.pages.storage.SortOption
import org.creategoodthings.vault.domain.repositories.ContainerSortOrder.*

class OfflinePreferencesRepository(
    private val dataStore: DataStore<Preferences>
): PreferencesRepository {
    private object Keys {
        val STANDARD_STORAGE_ID = stringPreferencesKey("standard_storage_id")
        val SORT_OPTION = stringPreferencesKey("sort_option")
        val REMINDER_TIME = stringPreferencesKey("reminder_time")
        val AM_PM = booleanPreferencesKey("amPm")
        val CONTAINER_SORT_ORDER = stringPreferencesKey("container_sort_order")
        val TOKEN = stringPreferencesKey("token") //TODO: This token HAS to be stored securely before any beta. See Log 08/01
    }

    override val standardStorageID = dataStore.data.map { preferences ->
        preferences[Keys.STANDARD_STORAGE_ID]
    }

    override val sortOption = dataStore.data.map { prefs ->
        val name = prefs[Keys.SORT_OPTION]
        if (name == null) null
        else SortOption.valueOf(name)
    }
    override val reminderTime = dataStore.data.map { prefs ->
        val time = prefs[Keys.REMINDER_TIME]
        if (time == null) LocalTime(8,0)
        else LocalTime.parse(time)
    }
    override val amPm = dataStore.data.map { it[Keys.AM_PM] == true }
    override val token = dataStore.data.map { it[Keys.TOKEN] }
    override val containerSortOrder = dataStore.data.map {
        val sortOrder = it[Keys.CONTAINER_SORT_ORDER]
        if (sortOrder != null) ContainerSortOrder.valueOf(sortOrder)
        else BEST_BEFORE
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

    override suspend fun setReminderTime(reminderTime: LocalTime) {
        dataStore.edit { it[Keys.REMINDER_TIME] = reminderTime.toString() }
    }

    override suspend fun setAmPm(amPm: Boolean) {
        dataStore.edit { it[Keys.AM_PM] = amPm }
    }

    override suspend fun setContainerSortOrder(sortOrder: ContainerSortOrder) {
        dataStore.edit { it[Keys.CONTAINER_SORT_ORDER] = sortOrder.toString() }
    }

    override suspend fun setToken(token: String) {
        dataStore.edit { it[Keys.TOKEN] = token }
    }

    override suspend fun clearToken() {
        dataStore.edit { it.remove(Keys.TOKEN) }
    }
}