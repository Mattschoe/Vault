package org.creategoodthings.vault.domain.services

class JvmPermissionController : PermissionController {
    override suspend fun hasNotificationPermission() = false //No notifications on Desktop
}