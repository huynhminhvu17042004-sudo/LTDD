package com.example.dncuik.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [IncomeEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun incomeDao(): IncomeDao
}
