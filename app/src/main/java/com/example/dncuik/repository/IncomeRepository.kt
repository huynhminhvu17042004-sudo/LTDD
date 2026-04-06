package com.example.dncuik.repository

import com.example.dncuik.data.IncomeDao
import com.example.dncuik.data.IncomeEntity
import com.example.dncuik.network.ExchangeRateService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IncomeRepository @Inject constructor(
    private val incomeDao: IncomeDao,
    private val apiService: ExchangeRateService
) {
    val allIncomes: Flow<List<IncomeEntity>> = incomeDao.getAllIncomes()

    suspend fun insertIncome(income: IncomeEntity) {
        incomeDao.insertIncome(income)
    }

    suspend fun deleteIncome(income: IncomeEntity) {
        incomeDao.deleteIncome(income)
    }

    suspend fun getExchangeRates(): Map<String, Double> = try {
        val response = apiService.getLatestRates()
        if (response.isSuccessful) {
            response.body()?.rates ?: emptyMap()
        } else {
            // Cập nhật giá trị thực tế hôm nay nếu API lỗi
            mapOf("VND" to 26388.0, "EUR" to 0.87) 
        }
    } catch (e: Exception) {
        // Giá trị dự phòng khi không có mạng
        mapOf("VND" to 26388.0, "EUR" to 0.87)
    }
}
