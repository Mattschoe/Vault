package org.creategoodthings.vault.domain.services

interface PermissionController {
    suspend fun hasNotificationPermission(): Boolean
}