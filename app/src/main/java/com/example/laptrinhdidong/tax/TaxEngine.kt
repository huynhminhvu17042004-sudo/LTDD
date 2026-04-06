package com.example.laptrinhdidong.tax

object TaxEngine {
    fun calculateIncomeTax(amount: Double, dependents: Int): Double {
        val reductionBase = 11000000.0
        val reductionDependent = 4400000.0
        val taxableAmount = amount - reductionBase - (dependents * reductionDependent)
        
        if (taxableAmount <= 0) return 0.0
        
        return when {
            taxableAmount <= 5000000 -> taxableAmount * 0.05
            taxableAmount <= 10000000 -> taxableAmount * 0.1 - 250000
            taxableAmount <= 18000000 -> taxableAmount * 0.15 - 750000
            taxableAmount <= 32000000 -> taxableAmount * 0.2 - 1650000
            taxableAmount <= 52000000 -> taxableAmount * 0.25 - 3250000
            taxableAmount <= 80000000 -> taxableAmount * 0.3 - 5850000
            else -> taxableAmount * 0.35 - 9850000
        }
    }

    fun calculateRentalTax(amount: Double): Double {
        // Thuế cho thuê tài sản thường là 10% (5% GTGT + 5% TNCN) nếu doanh thu > 100tr/năm
        return amount * 0.1
    }

    fun calculateInvestmentTax(amount: Double): Double {
        // Thuế đầu tư vốn thường là 5%
        return amount * 0.05
    }
}
