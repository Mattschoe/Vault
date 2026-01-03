package org.creategoodthings.vault.domain.services

import kotlinx.coroutines.flow.first
import kotlinx.datetime.LocalTime
import kotlinx.datetime.number
import org.creategoodthings.vault.domain.repositories.PreferencesRepository
import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarIdentifierGregorian
import platform.Foundation.NSDateComponents
import platform.UserNotifications.UNCalendarNotificationTrigger
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNUserNotificationCenter

class IOSNotificationScheduler(
    private val _prefRepo: PreferencesRepository
) : NotificationScheduler {
    private val notificationCenter = UNUserNotificationCenter.currentNotificationCenter()

    override suspend fun scheduleReminder(notification: NotificationData) {
        val reminderTime = _prefRepo.reminderTime.first()
        scheduleReminder(notification, reminderTime)
    }

    /**
     * [scheduleReminder]but with the reminderTime passed as argument to avoid excess database querying
     */
    private fun scheduleReminder(notification: NotificationData, reminderTime: LocalTime) {
        val content = UNMutableNotificationContent().apply {
            setTitle(notification.title)
            setBody(notification.message)
            setSound(UNNotificationSound.defaultSound)
        }

        val dateComponent = NSDateComponents().apply {
            setCalendar(NSCalendar(NSCalendarIdentifierGregorian))
            setYear(notification.date.year)
            setMonth(notification.date.month.number)
            setDay(notification.date.day)
            setHour(reminderTime.hour)
            setMinute(reminderTime.minute)
            setSecond(0)
        }

        val trigger = UNCalendarNotificationTrigger.triggerWithDateMatchingComponents(
            dateComponents = dateComponent,
            repeats = false
        )

        val request = UNNotificationRequest.requestWithIdentifier(
            identifier = notification.ID,
            content = content,
            trigger = trigger
        )

        notificationCenter.addNotificationRequest(request) { error ->
            if (error != null) {
                //TODO LOGGING
            }
        }
    }

    override fun cancelReminder(ID: String) {
        notificationCenter.removePendingNotificationRequestsWithIdentifiers(listOf(ID))
    }

    override suspend fun refreshNotifications(allNotifications: List<NotificationData>) {
        val reminderTime = _prefRepo.reminderTime.first()
        allNotifications.forEach { scheduleReminder(it, reminderTime) }
    }
}