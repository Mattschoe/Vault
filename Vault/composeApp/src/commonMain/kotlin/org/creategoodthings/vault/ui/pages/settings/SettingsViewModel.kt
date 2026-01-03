package org.creategoodthings.vault.ui.pages.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalTime
import org.creategoodthings.vault.domain.repositories.PreferencesRepository

class SettingsViewModel(
    private val _prefRepo: PreferencesRepository
): ViewModel() {

    val reminderTime = _prefRepo.reminderTime.stateIn(
        started = SharingStarted.WhileSubscribed(5_000),
        scope = viewModelScope,
        initialValue = LocalTime(8, 0)
    )

    val amPm = _prefRepo.amPm.stateIn(
        started = SharingStarted.WhileSubscribed(5_000),
        scope = viewModelScope,
        initialValue = false
    )

    fun setReminderTime(newTime: LocalTime) {
        viewModelScope.launch {
            _prefRepo.setReminderTime(newTime)
        }
    }

    fun setAmPm(amPm: Boolean) {
        viewModelScope.launch {
            _prefRepo.setAmPm(amPm)
        }
    }
}