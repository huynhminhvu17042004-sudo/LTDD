package com.example.laptrinhdidong.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "income_history")
data class IncomeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val sourceName: String,
    val sourceType: String,
    val dependents: Int,
    val taxAmount: Double,
    val netAmount: Double,
    val insuranceAmount: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis()
)
