package com.example.dncuik.service

import android.app.PendingIntent
import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.dncuik.R
import com.example.dncuik.util.TransactionParser

class BankNotificationListener : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        val packageName = sbn?.packageName ?: return
        val extras = sbn.notification.extras
        val text = extras.getCharSequence("android.text")?.toString() ?: ""

        if (isBankApp(packageName)) {
            val parsed = TransactionParser.parse(text)
            if (parsed != null) {
                showQuickSaveNotification(parsed.amount, parsed.type, parsed.description)
            }
        }
    }

    private fun showQuickSaveNotification(amount: Double, type: String, desc: String) {
        val intent = Intent(this, TransactionReceiver::class.java).apply {
            putExtra("amount", amount)
            putExtra("type", type)
            putExtra("desc", desc)
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            this, amount.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, "BANK_ALERTS")
            .setSmallIcon(android.R.drawable.ic_menu_save)
            .setContentTitle("Phát hiện thu nhập mới!")
            .setContentText("Số tiền: ${String.format("%,.0f", amount)}đ từ $desc")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .addAction(android.R.drawable.ic_menu_add, "LƯU NHANH VÀO THUẾ", pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(this).notify(amount.toInt(), notification)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    private fun isBankApp(packageName: String): Boolean {
        val banks = listOf("vietcombank", "techcombank", "mbmobile", "momo", "vnpay", "mservice")
        return banks.any { packageName.contains(it, ignoreCase = true) }
    }
}
