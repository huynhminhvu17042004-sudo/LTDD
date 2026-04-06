package com.example.laptrinhdidong.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class BankNotificationListener : NotificationListenerService() {
    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val packageName = sbn.packageName
        val extras = sbn.notification.extras
        val title = extras.getString("android.title")
        val text = extras.getCharSequence("android.text")?.toString()

        Log.d("BankListener", "Notification from: $packageName, Title: $title, Text: $text")
        
        // Example logic to filter bank apps
        if (packageName.contains("com.vcb") || packageName.contains("com.tpb")) {
            Log.d("BankListener", "Detected bank transaction notification!")
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        // Handle notification removal if needed
    }
}
