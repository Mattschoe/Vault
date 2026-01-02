package org.creategoodthings.vault.domain.services

import kotlinx.datetime.LocalDate
import kotlinx.datetime.number
import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarIdentifier
import platform.Foundation.NSCalendarIdentifierGregorian
import platform.Foundation.NSDateComponents
import platform.UserNotifications.UNCalendarNotificationTrigger
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNUserNotificationCenter

class IOSNotificationScheduler : NotificationScheduler {
    private val notificationCenter = UNUserNotificationCenter.currentNotificationCenter()

    override fun scheduleReminder(notification: NotificationData) {
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
            setHour(8)
            setMinute(0)
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

    override fun refreshNotifications(allNotifications: List<NotificationData>) {
        allNotifications.forEach { scheduleReminder(it) }
    }
}