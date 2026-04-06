package com.example.laptrinhdidong.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.laptrinhdidong.data.IncomeDao
import com.example.laptrinhdidong.data.IncomeEntity
import com.example.laptrinhdidong.tax.TaxEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val incomeDao: IncomeDao
) : ViewModel() {
    val incomeHistory: StateFlow<List<IncomeEntity>> = incomeDao.getAllIncomes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _editingIncome = MutableStateFlow<IncomeEntity?>(null)
    val editingIncome: StateFlow<IncomeEntity?> = _editingIncome

    private val _rates = MutableStateFlow<Map<String, Double>>(mapOf("VND" to 25450.0, "EUR" to 27000.0))
    val rates: StateFlow<Map<String, Double>> = _rates

    fun addIncome(amount: Double, sourceName: String, sourceType: String, dependents: Int) {
        viewModelScope.launch {
            val taxAmount = when (sourceType) {
                "SALARY" -> TaxEngine.calculateIncomeTax(amount, dependents)
                "RENTAL" -> TaxEngine.calculateRentalTax(amount)
                "INVESTMENT" -> TaxEngine.calculateInvestmentTax(amount)
                else -> amount * 0.1
            }
            
            val newIncome = if (_editingIncome.value != null) {
                _editingIncome.value!!.copy(
                    amount = amount,
                    sourceName = sourceName,
                    sourceType = sourceType,
                    dependents = dependents,
                    taxAmount = taxAmount,
                    netAmount = amount - taxAmount,
                    timestamp = System.currentTimeMillis()
                )
            } else {
                IncomeEntity(
                    amount = amount,
                    sourceName = sourceName,
                    sourceType = sourceType,
                    dependents = dependents,
                    taxAmount = taxAmount,
                    netAmount = amount - taxAmount,
                    timestamp = System.currentTimeMillis()
                )
            }
            
            incomeDao.insertIncome(newIncome)
            _editingIncome.value = null
        }
    }

    fun deleteIncome(item: IncomeEntity) {
        viewModelScope.launch {
            incomeDao.deleteIncome(item)
        }
    }

    fun setEditingIncome(item: IncomeEntity?) {
        _editingIncome.value = item
    }

    fun exportToExcel(context: Context) {
        // Chức năng tạm thời để trống để tránh lỗi
    }

    // Đã chuyển logic tính toán vào TaxEngine
}
