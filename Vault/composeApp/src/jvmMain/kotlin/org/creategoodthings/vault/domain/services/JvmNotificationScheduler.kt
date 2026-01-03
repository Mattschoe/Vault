package org.creategoodthings.vault.domain.services

class JvmNotificationScheduler : NotificationScheduler {
    override suspend fun scheduleReminder(notification: NotificationData) {
        //NO OP on Desktop
    }

    override fun cancelReminder(ID: String) {
        //NO OP on Desktop
    }

    override suspend fun refreshNotifications(allNotifications: List<NotificationData>) {
        //NO OP on Desktop
    }
}