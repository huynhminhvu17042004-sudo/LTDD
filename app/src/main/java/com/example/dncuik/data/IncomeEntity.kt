package com.example.dncuik.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "income_history")
data class IncomeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val amount: Double,
    val sourceName: String,
    val sourceType: String,
    val timestamp: Long = System.currentTimeMillis(),
    val dependents: Int = 0,
    val insuranceAmount: Double = 0.0,
    val taxableIncome: Double = 0.0,
    val taxAmount: Double = 0.0,
    val netAmount: Double = 0.0,
    val currency: String = "VND"
)
