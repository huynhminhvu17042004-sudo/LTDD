package com.example.dncuik.tax

import org.junit.Assert.assertEquals
import org.junit.Test

class TaxEngineTest {
    private val engine = TaxEngine()
    private val profile = TaxProfile()

    @Test
    fun `test low salary - no tax`() {
        // Lương 5,000,000đ - Sau khi trừ bảo hiểm (10.5%) vẫn dưới mức giảm trừ gia cảnh (11tr)
        val result = engine.calculate(5000000.0, profile, 0, "SALARY")
        assertEquals(0.0, result.taxAmount, 0.1)
    }

    @Test
    fun `test average salary - with tax`() {
        // Lương 20,000,000đ
        // Bảo hiểm: 2,100,000đ
        // Thu nhập còn lại: 17,900,000đ
        // Giảm trừ: 11,000,000đ -> Thu nhập tính thuế: 6,900,000đ
        // Thuế bậc 1 (5tr * 5%): 250,000đ
        // Thuế bậc 2 (1.9tr * 10%): 190,000đ
        // Tổng thuế: 440,000đ
        val result = engine.calculate(20000000.0, profile, 0, "SALARY")
        assertEquals(440000.0, result.taxAmount, 0.1)
    }

    @Test
    fun `test rental income tax - flat 10 percent`() {
        // Thu nhập thuê nhà 10,000,000đ -> Thuế phẳng 10% = 1,000,000đ
        val result = engine.calculate(10000000.0, profile, 0, "RENTAL")
        assertEquals(1000000.0, result.taxAmount, 0.1)
    }
}
