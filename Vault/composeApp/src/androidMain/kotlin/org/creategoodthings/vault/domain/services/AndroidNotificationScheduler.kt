package org.creategoodthings.vault.domain.services

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import kotlinx.coroutines.flow.first
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.number
import org.creategoodthings.vault.domain.repositories.PreferencesRepository

class AndroidNotificationScheduler(
    private val context: Context,
    private val prefRepo: PreferencesRepository
) : NotificationScheduler {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    init {
        //Creates notification channel
        val name = "Daily Reminders"
        val descriptionText = "Notifications for product expiry"
        val importance = NotificationManager.IMPORTANCE_DEFAULT

        val channel = NotificationChannel("DAILY_REMINDERS", name, importance).apply {
            description = descriptionText
        }
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    override suspend fun scheduleReminder(notification: NotificationData) {
        val reminderTime = prefRepo.reminderTime.first()
        scheduleReminder(notification, reminderTime)
    }

    /**
     * [scheduleReminder] but with the reminderTime passed as argument to avoid excess database querying
     */
    private fun scheduleReminder(notification: NotificationData, reminderTime: LocalTime) {
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("TITLE", notification.title)
            putExtra("MESSAGE", notification.message)
            putExtra("PRODUCT_ID", notification.ID) //Used in NotificationReceiver
        }

        val requestCode = notification.ID.hashCode()
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, notification.date.year)
            set(Calendar.MONTH, notification.date.month.number - 1)
            set(Calendar.DAY_OF_MONTH, notification.date.day)
            set(Calendar.HOUR_OF_DAY, reminderTime.hour) //TODO: User should be able to change this themselves
            set(Calendar.MINUTE, reminderTime.minute)
            set(Calendar.SECOND, 0)
        }

        //Avoid scheduling in the past
        if (calendar.timeInMillis < System.currentTimeMillis()) return

        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }

    override fun cancelReminder(ID: String) {
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, ID.hashCode(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    override suspend fun refreshNotifications(allNotifications: List<NotificationData>) {
        val reminderTime = prefRepo.reminderTime.first()
        allNotifications.forEach {
            scheduleReminder(it, reminderTime)
        }
    }
}