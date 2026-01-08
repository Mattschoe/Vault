package org.creategoodthings.vault.domain.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import org.creategoodthings.vault.ui.pages.storage.SortOption
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
interface PreferencesRepository {
    val standardStorageID: Flow<String?>
    val sortOption: Flow<SortOption?>

    //region SETTINGS
    val reminderTime: Flow<LocalTime>
    val amPm: Flow<Boolean>
    val containerSortOrder: Flow<ContainerSortOrder>
    //endregion

    //region NETWORKING
    val token: Flow<String?>
    val userID: Flow<String?>
    val lastSync: Flow<Instant?>
    //endregion

    suspend fun setStandardStorageID(storageID: String)
    suspend fun setSortOption(sortOption: SortOption)
    suspend fun setReminderTime(reminderTime: LocalTime)
    suspend fun setAmPm(amPm: Boolean)
    suspend fun setContainerSortOrder(sortOrder: ContainerSortOrder)
    suspend fun setToken(token: String)
    suspend fun clearToken()
    suspend fun setUserID(userID: String)
    suspend fun clearUserID()
    suspend fun setLastSync(newTime: Instant)
}

enum class ContainerSortOrder {
    ALPHABETICALLY,
    BEST_BEFORE
}