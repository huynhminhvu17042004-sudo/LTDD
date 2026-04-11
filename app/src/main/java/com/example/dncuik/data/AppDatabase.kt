package com.example.dncuik.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [IncomeEntity::class, UserEntity::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun incomeDao(): IncomeDao
    abstract fun userDao(): UserDao
}
