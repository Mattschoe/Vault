package org.creategoodthings.vault.domain.services

import android.Manifest
import android.R
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class NotificationReceiver : BroadcastReceiver() {
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("TITLE") ?: "Reminder"
        val message = intent.getStringExtra("MESSAGE") ?: ""

        val builder = NotificationCompat.Builder(context, "DAILY_REMINDERS")
            .setSmallIcon(R.drawable.ic_dialog_info) //TODO: CHANGE ICON
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        try {
            val notificationId = intent.getStringExtra("PRODUCT_ID")?.hashCode() ?: System.currentTimeMillis().toInt()
            NotificationManagerCompat
                .from(context)
                .notify(notificationId, builder.build())
        } catch (e: SecurityException) {
            println(e) //No permission
        }
    }
}