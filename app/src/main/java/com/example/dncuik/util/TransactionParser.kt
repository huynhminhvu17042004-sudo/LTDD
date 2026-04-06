package com.example.dncuik.util

data class ParsedTransaction(
    val amount: Double,
    val type: String,
    val description: String
)

object TransactionParser {
    // Regex tìm số tiền (VD: +1,000,000 hoặc +5.000.000)
    private val amountRegex = Regex("""(\+)\s?([0-9.,]{4,})""")

    fun parse(text: String): ParsedTransaction? {
        // 1. Tìm số tiền
        val match = amountRegex.find(text) ?: return null
        val amountStr = match.groupValues[2]
            .replace(".", "")
            .replace(",", "")
        
        val amount = amountStr.toDoubleOrNull() ?: return null
        if (amount <= 0) return null

        // 2. Phân loại dựa trên từ khóa
        val type = when {
            text.contains("luong", ignoreCase = true) || 
            text.contains("salary", ignoreCase = true) ||
            text.contains("PAYROLL", ignoreCase = true) -> "SALARY"
            
            text.contains("thue nha", ignoreCase = true) || 
            text.contains("thue mat bang", ignoreCase = true) -> "RENTAL"
            
            text.contains("co tuc", ignoreCase = true) || 
            text.contains("lai tiet kiem", ignoreCase = true) -> "INVESTMENT"
            
            else -> "OTHERS"
        }

        // 3. Trích xuất mô tả ngắn
        val description = if (text.length > 30) text.take(30) + "..." else text

        return ParsedTransaction(amount, type, description)
    }
}
