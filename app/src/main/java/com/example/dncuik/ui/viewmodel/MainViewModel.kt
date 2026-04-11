package com.example.dncuik.ui.viewmodel

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dncuik.data.IncomeEntity
import com.example.dncuik.data.UserEntity
import com.example.dncuik.repository.IncomeRepository
import com.example.dncuik.repository.UserRepository
import com.example.dncuik.tax.TaxEngine
import com.example.dncuik.tax.TaxProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: IncomeRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val taxEngine = TaxEngine()
    
    val incomeHistory = repository.allIncomes.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    private val _rates = MutableStateFlow<Map<String, Double>>(emptyMap())
    val rates: StateFlow<Map<String, Double>> = _rates

    private val _editingIncome = MutableStateFlow<IncomeEntity?>(null)
    val editingIncome = _editingIncome.asStateFlow()

    private val _loginState = MutableStateFlow(false)
    val loginState = _loginState.asStateFlow()

    init {
        fetchRates()
    }

    fun login(username: String, password: String, onResult: (Boolean, String) -> Unit) {
        if (username.isBlank() || password.isBlank()) {
            onResult(false, "Vui lòng điền đầy đủ thông tin")
            return
        }
        viewModelScope.launch {
            val user = userRepository.getUserByUsername(username)
            if (user != null && user.password == password) {
                _loginState.value = true
                onResult(true, "Đăng nhập thành công")
            } else {
                onResult(false, "Sai tên đăng nhập hoặc mật khẩu")
            }
        }
    }

    fun register(username: String, password: String, onResult: (Boolean, String) -> Unit) {
        if (username.isBlank() || password.isBlank()) {
            onResult(false, "Vui lòng nhập đầy đủ thông tin")
            return
        }
        if (password.length < 8) {
            onResult(false, "Mật khẩu phải có ít nhất 8 ký tự")
            return
        }
        // Kiểm tra tính phức tạp: ít nhất 1 chữ cái và 1 con số
        val passwordRegex = "^(?=.*[A-Za-z])(?=.*\\d).{8,}$".toRegex()
        if (!password.matches(passwordRegex)) {
            onResult(false, "Mật khẩu cần bao gồm cả chữ và số")
            return
        }

        viewModelScope.launch {
            try {
                userRepository.registerUser(UserEntity(username, password))
                onResult(true, "Đăng ký thành công")
            } catch (e: Exception) {
                onResult(false, "Tên đăng nhập đã tồn tại")
            }
        }
    }

    fun logout() {
        _loginState.value = false
    }

    fun fetchRates() {
        viewModelScope.launch {
            _rates.value = repository.getExchangeRates()
        }
    }

    fun addIncome(amount: Double, sourceName: String, type: String, dependents: Int) {
        viewModelScope.launch {
            val res = taxEngine.calculate(amount, TaxProfile(), dependents, type)
            val currentEditing = _editingIncome.value
            
            val entity = IncomeEntity(
                id = currentEditing?.id ?: 0,
                amount = amount,
                sourceName = sourceName,
                sourceType = type,
                dependents = dependents,
                insuranceAmount = res.insurance,
                taxableIncome = res.taxableIncome,
                taxAmount = res.taxAmount,
                netAmount = res.net,
                timestamp = currentEditing?.timestamp ?: System.currentTimeMillis()
            )
            repository.insertIncome(entity)
            _editingIncome.value = null
        }
    }

    fun deleteIncome(income: IncomeEntity) {
        viewModelScope.launch {
            repository.deleteIncome(income)
        }
    }

    fun setEditingIncome(income: IncomeEntity?) {
        _editingIncome.value = income
    }

    fun exportToExcel(context: Context) {
        val data = incomeHistory.value
        if (data.isEmpty()) return

        val fileName = "BaoCaoThuNhap.csv"
        val file = File(context.filesDir, fileName)
        val header = "Ten nguon,Loai,So tien,Thue,Thuc nhan\n"
        val content = StringBuilder(header)
        
        data.forEach {
            content.append("${it.sourceName},${it.sourceType},${it.amount},${it.taxAmount},${it.netAmount}\n")
        }

        try {
            file.writeText(content.toString())
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/csv"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Chia sẻ báo cáo qua..."))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
