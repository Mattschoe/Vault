package org.creategoodthings.vault.data.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalTime
import org.creategoodthings.vault.data.repositories.OfflinePreferencesRepository.Keys.LAST_SYNC
import org.creategoodthings.vault.data.repositories.OfflinePreferencesRepository.Keys.USER_ID
import org.creategoodthings.vault.domain.repositories.ContainerSortOrder
import org.creategoodthings.vault.domain.repositories.ContainerSortOrder.BEST_BEFORE
import org.creategoodthings.vault.domain.repositories.PreferencesRepository
import org.creategoodthings.vault.ui.pages.storage.SortOption
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
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
        val USER_ID = stringPreferencesKey("user_id")
        val LAST_SYNC = stringPreferencesKey("last_sync")
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
    override val lastSync = dataStore.data.map {
        val syncTime = it[LAST_SYNC]
        if (syncTime == null) null
        else Instant.parse(syncTime)
    }
    override val userID = dataStore.data.map { it[USER_ID] }

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

    override suspend fun clearUserID() {
        dataStore.edit { it.remove(USER_ID) }
    }

    override suspend fun setLastSync(newTime: Instant) {
        dataStore.edit { it[LAST_SYNC] = newTime.toString() }
    }

    override suspend fun setUserID(userID: String) {
        dataStore.edit { it[USER_ID] = userID }
    }
}