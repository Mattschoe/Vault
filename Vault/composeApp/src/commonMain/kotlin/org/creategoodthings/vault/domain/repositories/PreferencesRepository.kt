package org.creategoodthings.vault.domain.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalTime
import org.creategoodthings.vault.ui.pages.storage.SortOption

interface PreferencesRepository {
    val standardStorageID: Flow<String?>
    val sortOption: Flow<SortOption?>

    //region SETTINGS
    val reminderTime: Flow<LocalTime>
    val amPm: Flow<Boolean>
    val containerSortOrder: Flow<ContainerSortOrder>
    //endregion

    suspend fun setStandardStorageID(storageID: String)
    suspend fun setSortOption(sortOption: SortOption)
    suspend fun setReminderTime(reminderTime: LocalTime)
    suspend fun setAmPm(amPm: Boolean)
    suspend fun setContainerSortOrder(sortOrder: ContainerSortOrder)
}

enum class ContainerSortOrder {
    ALPHABETICALLY,
    BEST_BEFORE
}