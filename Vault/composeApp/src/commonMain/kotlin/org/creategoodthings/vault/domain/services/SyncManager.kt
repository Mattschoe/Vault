package org.creategoodthings.vault.domain.services

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.creategoodthings.vault.domain.Result
import org.creategoodthings.vault.domain.SyncError
import org.creategoodthings.vault.domain.repositories.SyncRepository

class SyncManager(
    private val _syncRepo: SyncRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing = _isSyncing.asStateFlow() //TODO skal propargates op til UIww

    private val _lastError = MutableStateFlow<SyncError?>(null)
    val lastError = _lastError.asStateFlow()

    /**
     * Syncs all products to the cloud, this is only done if the manager is not already syncing
     */
     fun startSync() {
        if (_isSyncing.value) return

        scope.launch {
            _isSyncing.value = true
            _lastError.value = null

            when(val result = _syncRepo.sync()) {
                is Result.Error -> _lastError.value = result.error
                is Result.Success -> {
                    _isSyncing.value = false
                }
            }
        }
    }
}