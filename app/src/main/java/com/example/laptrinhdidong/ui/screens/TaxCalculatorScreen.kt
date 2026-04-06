package com.example.laptrinhdidong.ui.screens

import android.R
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import com.example.laptrinhdidong.ui.theme.PrimaryBlue
import com.example.laptrinhdidong.ui.theme.SecondaryBlue
import com.example.laptrinhdidong.ui.theme.SuccessGreen
import com.example.laptrinhdidong.ui.viewmodel.MainViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaxCalculatorScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val editingIncome by viewModel.editingIncome.collectAsState()
    var amount by remember { mutableStateOf("") }
    var sourceName by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("SALARY") }
    var dependents by remember { mutableStateOf("0") }
    var showCamera by remember { mutableStateOf(false) }

    if (showCamera) {
        /* CameraScreen missing - placeholder */
        showCamera = false
        return
    }
    
    LaunchedEffect(editingIncome) {
        editingIncome?.let {
            amount = it.amount.toString()
            sourceName = it.sourceName
            type = it.sourceType
            dependents = it.dependents.toString()
        }
    }

    val rates by viewModel.rates.collectAsState()

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(32.dp))
            Spacer(Modifier.width(8.dp))
            Text(
                if (editingIncome != null) "Chỉnh Sửa Thu Nhập" else "Thuế Thu Nhập",
                style = MaterialTheme.typography.headlineSmall, 
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue
            )
        }
        
        // Tỷ giá Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("Tỷ giá hôm nay (Tham khảo)", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                    val vndRate = rates["VND"] ?: 0.0
                    val eurRate = rates["EUR"] ?: 0.0

                    // Vì API Base là USD, nên rates["VND"] chính là giá trị 1 USD ra VND
                    val displayUsd = if (vndRate > 0) String.format("%,.0fđ", vndRate) else "..."
                    // 1 EUR = (1/eurRate) * vndRate
                    val displayEur = if (eurRate > 0 && vndRate > 0) String.format("%,.0fđ", vndRate / eurRate) else "..."

                    CurrencyChip("USD", displayUsd, Icons.Default.AttachMoney)
                    CurrencyChip("EUR", displayEur, Icons.Default.Euro)
                }
            }
        }

        OutlinedTextField(
            value = sourceName,
            onValueChange = { sourceName = it },
            label = { Text("Tên nguồn thu (Vd: Lương tháng 10)") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            leadingIcon = { Icon(Icons.Default.EditNote, null) }
        )

        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Số tiền (VND)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = { Icon(Icons.Default.Payments, null) }
            )
            Spacer(Modifier.width(8.dp))
            IconButton(
                onClick = { showCamera = true },
                modifier = Modifier.size(56.dp).background(SecondaryBlue, RoundedCornerShape(12.dp))
            ) {
                Icon(Icons.Default.PhotoCamera, "Quét hóa đơn", tint = PrimaryBlue)
            }
        }

        Text("Loại hình thu nhập", fontWeight = FontWeight.Medium, style = MaterialTheme.typography.bodyLarge)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TypeChip("Lương", type == "SALARY") { type = "SALARY" }
            TypeChip("Thuê nhà", type == "RENTAL") { type = "RENTAL" }
            TypeChip("Đầu tư", type == "INVESTMENT") { type = "INVESTMENT" }
        }

        if (type == "SALARY") {
            OutlinedTextField(
                value = dependents,
                onValueChange = { dependents = it },
                label = { Text("Số người phụ thuộc") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = { Icon(Icons.Default.Groups, null) }
            )
        }

        Spacer(Modifier.height(8.dp))

        // Nút mô phỏng tạm thời vô hiệu hóa do thiếu TaxEngine
        OutlinedButton(
            onClick = { /* Missing TaxEngine */ },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            enabled = false
        ) {
            Icon(Icons.Default.BugReport, null)
            Spacer(Modifier.width(8.dp))
            Text("MÔ PHỎNG NHẬN TIỀN (TEST)")
        }

        Button(
            onClick = {
                val valAmount = amount.toDoubleOrNull() ?: 0.0
                val valDeps = dependents.toIntOrNull() ?: 0
                if (valAmount > 0) {
                    viewModel.addIncome(valAmount, sourceName, type, valDeps)
                    amount = ""; sourceName = ""
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
            Icon(if (editingIncome != null) Icons.Default.Save else Icons.Default.AddTask, null)
            Spacer(Modifier.width(8.dp))
            Text(if (editingIncome != null) "CẬP NHẬT DỮ LIỆU" else "TÍNH VÀ LƯU LỊCH SỬ", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun CurrencyChip(label: String, value: String, icon: ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(4.dp))
        Text(label, fontWeight = FontWeight.Bold)
        Text(": $value", color = Color.DarkGray)
    }
}

@Composable
fun TypeChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = { Text(label) },
        shape = RoundedCornerShape(20.dp),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = PrimaryBlue,
            selectedLabelColor = Color.White
        )
    )
}
