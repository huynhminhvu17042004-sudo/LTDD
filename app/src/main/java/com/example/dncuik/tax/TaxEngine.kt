package com.example.dncuik.tax

import com.example.dncuik.data.IncomeEntity

data class TaxProfile(
    val selfDeduction: Double = 11000000.0,
    val dependentDeduction: Double = 4400000.0,
    val insuranceRate: Double = 0.105
)

data class TaxResult(
    val gross: Double,
    val insurance: Double,
    val taxableIncome: Double,
    val taxAmount: Double,
    val net: Double,
    val type: String = "SALARY"
)

class TaxEngine {
    fun calculate(gross: Double, profile: TaxProfile, dependentCount: Int, type: String = "SALARY"): TaxResult {
        return when (type) {
            "SALARY" -> calculateSalaryTax(gross, profile, dependentCount)
            "RENTAL" -> calculateRentalTax(gross)
            "INVESTMENT" -> calculateInvestmentTax(gross)
            else -> calculateSalaryTax(gross, profile, dependentCount)
        }
    }

    private fun calculateSalaryTax(gross: Double, profile: TaxProfile, dependentCount: Int): TaxResult {
        val insurance = gross * profile.insuranceRate
        val incomeBeforeDeduction = (gross - insurance).coerceAtLeast(0.0)
        val totalDeduction = profile.selfDeduction + (dependentCount * profile.dependentDeduction)
        val taxableIncome = (incomeBeforeDeduction - totalDeduction).coerceAtLeast(0.0)
        
        val tax = when {
            taxableIncome <= 5_000_000 -> taxableIncome * 0.05
            taxableIncome <= 10_000_000 -> taxableIncome * 0.1 - 250_000
            taxableIncome <= 18_000_000 -> taxableIncome * 0.15 - 750_000
            taxableIncome <= 32_000_000 -> taxableIncome * 0.2 - 1_650_000
            taxableIncome <= 52_000_000 -> taxableIncome * 0.25 - 3_250_000
            taxableIncome <= 80_000_000 -> taxableIncome * 0.3 - 5_850_000
            else -> taxableIncome * 0.35 - 9_850_000
        }
        return TaxResult(gross, insurance, taxableIncome, tax, gross - insurance - tax, "SALARY")
    }

    private fun calculateRentalTax(gross: Double): TaxResult {
        // Thuế cho thuê nhà: 5% GTGT + 5% TNCN = 10%
        val tax = gross * 0.10
        return TaxResult(gross, 0.0, gross, tax, gross - tax, "RENTAL")
    }

    private fun calculateInvestmentTax(gross: Double): TaxResult {
        // Thuế đầu tư vốn: 5%
        val tax = gross * 0.05
        return TaxResult(gross, 0.0, gross, tax, gross - tax, "INVESTMENT")
    }
}
