package com.example.dncuik.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.dncuik.data.IncomeEntity
import com.example.dncuik.repository.IncomeRepository
import com.example.dncuik.tax.TaxEngine
import com.example.dncuik.tax.TaxProfile
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TransactionReceiver : BroadcastReceiver() {

    @Inject lateinit var repository: IncomeRepository
    private val taxEngine = TaxEngine()

    override fun onReceive(context: Context?, intent: Intent?) {
        val amount = intent?.getDoubleExtra("amount", 0.0) ?: 0.0
        val type = intent?.getStringExtra("type") ?: "OTHERS"
        val desc = intent?.getStringExtra("desc") ?: "Giao dịch tự động"

        if (amount > 0) {
            CoroutineScope(Dispatchers.IO).launch {
                val res = taxEngine.calculate(amount, TaxProfile(), 0, type)
                val entity = IncomeEntity(
                    amount = amount,
                    sourceName = desc,
                    sourceType = type,
                    taxAmount = res.taxAmount,
                    netAmount = res.net,
                    insuranceAmount = res.insurance,
                    taxableIncome = res.taxableIncome
                )
                repository.insertIncome(entity)
            }
        }
    }
}
