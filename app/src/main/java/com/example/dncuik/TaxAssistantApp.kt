package com.example.dncuik

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TaxAssistantApp : Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "TaxVND Transaction Alerts"
            val descriptionText = "Thông báo khi phát hiện biến động số dư ngân hàng"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("BANK_ALERTS", name, importance).apply {
                description = descriptionText
                enableLights(true)
                enableVibration(true)
                setSound(android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_NOTIFICATION), null)
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            // Base application class with DI setup
        }
    }
}
