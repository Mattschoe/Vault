package org.creategoodthings.vault.domain.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.creategoodthings.vault.AppContainer
import org.creategoodthings.vault.MainActivity
import org.creategoodthings.vault.MyApplication

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val app = context.applicationContext as MyApplication
            val productRepo = app.appContainer.productRepo
            val scheduler = app.appContainer.notificationScheduler

            val pendingResult = goAsync() //Prevents Android from exiting since we are reading disk

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val products = productRepo.getAllProducts().first()
                    val notifications = products.map { product ->
                        NotificationData( //TODO better text/language
                            ID = product.ID,
                            title = "Expires soon: ${product.name}",
                            message = product.description,
                            date = product.reminderDate
                        )
                    }
                    scheduler.refreshNotifications(notifications)
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}