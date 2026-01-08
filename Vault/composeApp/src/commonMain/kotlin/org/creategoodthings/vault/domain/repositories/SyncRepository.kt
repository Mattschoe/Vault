package org.creategoodthings.vault.domain.repositories

import org.creategoodthings.vault.domain.Result
import org.creategoodthings.vault.domain.SyncError

interface SyncRepository {
    suspend fun sync(): Result<Unit, SyncError>
}