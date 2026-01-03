package org.creategoodthings.vault.domain.services

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

interface NotificationScheduler {
    suspend fun scheduleReminder(notification: NotificationData)
    fun cancelReminder(ID: String)
    suspend fun refreshNotifications(allNotifications: List<NotificationData>)
}

data class NotificationData(
    val ID: String,
    val title: String,
    val message: String,
    val date: LocalDate
)