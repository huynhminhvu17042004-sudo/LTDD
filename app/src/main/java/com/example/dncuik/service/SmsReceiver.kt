package com.example.dncuik.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage
import android.util.Log

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "android.provider.Telephony.SMS_RECEIVED") {
            val bundle = intent.extras
            if (bundle != null) {
                val pdus = bundle.get("pdus") as Array<*>
                for (pdu in pdus) {
                    val message = SmsMessage.createFromPdu(pdu as ByteArray)
                    val sender = message.displayOriginatingAddress
                    val content = message.displayMessageBody

                    // Kiểm tra xem có phải tin nhắn ngân hàng không
                    if (isBankSms(sender, content)) {
                        Log.d("SmsReceiver", "Tin nhắn từ: $sender")
                        Log.d("SmsReceiver", "Nội dung: $content")
                        
                        // Ở giai đoạn 2 sẽ xử lý phân tích nội dung ở đây
                    }
                }
            }
        }
    }

    private fun isBankSms(sender: String, content: String): Boolean {
        val bankKeywords = listOf("VCB", "Techcombank", "MBBank", "VietinBank", "BIDV", "Agribank", "ACBBank", "TPBank")
        val contentKeywords = listOf("so du", "bien dong", "giao dich", "VND", "han muc")
        
        return bankKeywords.any { sender.contains(it, ignoreCase = true) } || 
               contentKeywords.all { content.contains(it, ignoreCase = true) }
    }
}
