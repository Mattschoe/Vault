package org.creategoodthings.vault.domain.services

import kotlinx.datetime.LocalDate

interface NotificationScheduler {
    fun scheduleReminder(notification: NotificationData)
    fun cancelReminder(ID: String)
    fun refreshNotifications(allNotifications: List<NotificationData>)
}

data class NotificationData(
    val ID: String,
    val title: String,
    val message: String,
    val date: LocalDate
)