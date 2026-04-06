package com.example.dncuik.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface IncomeDao {
    @Query("SELECT * FROM income_history ORDER BY timestamp DESC")
    fun getAllIncomes(): Flow<List<IncomeEntity>>

    @Query("SELECT * FROM income_history WHERE sourceType = :type")
    fun getIncomesByType(type: String): Flow<List<IncomeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncome(income: IncomeEntity)

    @Delete
    suspend fun deleteIncome(income: IncomeEntity)

    @Query("SELECT SUM(amount) FROM income_history WHERE timestamp >= :startTime")
    suspend fun getTotalIncomeSince(startTime: Long): Double?
}
