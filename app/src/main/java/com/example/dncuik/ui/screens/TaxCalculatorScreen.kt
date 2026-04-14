package com.example.dncuik.ui.screens

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import android.app.NotificationManager
import com.example.dncuik.ui.theme.PrimaryBlue
import com.example.dncuik.ui.theme.SecondaryBlue
import com.example.dncuik.ui.theme.SuccessGreen
import com.example.dncuik.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaxCalculatorScreen(viewModel: MainViewModel) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val editingIncome by viewModel.editingIncome.collectAsState()
    var amount by remember { mutableStateOf("") }
    var sourceName by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("SALARY") }
    var dependents by remember { mutableStateOf("0") }
    var showCamera by remember { mutableStateOf(false) }

    if (showCamera) {
        com.example.dncuik.ui.camera.CameraScreen(
            onResult = { detectedAmount, detectedVendor, _ ->
                amount = String.format("%.0f", detectedAmount)
                if (sourceName.isBlank()) {
                    sourceName = detectedVendor
                }
                showCamera = false
            },
            onClose = { showCamera = false }
        )
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
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Header Section with Welcome
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Tính toán Thuế",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                )
                Text(
                    text = "Nhập thu nhập để kiểm tra thuế",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = SecondaryBlue.copy(alpha = 0.2f)
            ) {
                Icon(
                    Icons.Default.Calculate,
                    contentDescription = null,
                    modifier = Modifier.padding(10.dp),
                    tint = PrimaryBlue
                )
            }
        }

        // Exchange Rates Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.TrendingUp, null, tint = SuccessGreen, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Tỷ giá hôm nay", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val vndRate = rates["VND"] ?: 25450.0
                    val eurRate = rates["EUR"] ?: 0.92
                    
                    CurrencyInfoBox("USD/VND", String.format("%,.0fđ", vndRate), Icons.Default.AttachMoney)
                    Divider(modifier = Modifier.height(30.dp).width(1.dp).align(Alignment.CenterVertically))
                    CurrencyInfoBox("EUR/VND", String.format("%,.0fđ", vndRate/eurRate), Icons.Default.Euro)
                }
            }
        }

        // Input Form
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Thông tin thu nhập", fontWeight = FontWeight.Bold, color = Color.DarkGray)

                OutlinedTextField(
                    value = sourceName,
                    onValueChange = { sourceName = it },
                    label = { Text("Tên nguồn thu nhập") },
                    placeholder = { Text("Ví dụ: Lương tháng 12") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = { Icon(Icons.Default.EditNote, null, tint = PrimaryBlue) }
                )

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        label = { Text("Số tiền (VND)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = { Icon(Icons.Default.Payments, null, tint = PrimaryBlue) }
                    )
                    
                    Surface(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(PrimaryBlue),
                        onClick = { showCamera = true }
                    ) {
                        Icon(
                            Icons.Default.PhotoCamera,
                            "Quét hóa đơn",
                            tint = Color.White,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                Text("Hình thức", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val types = listOf("SALARY" to "Lương", "RENTAL" to "Thuê nhà", "INVESTMENT" to "Đầu tư")
                    types.forEach { (key, label) ->
                        FilterChip(
                            selected = type == key,
                            onClick = { type = key },
                            label = { Text(label) },
                            shape = RoundedCornerShape(10.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryBlue,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                if (type == "SALARY") {
                    OutlinedTextField(
                        value = dependents,
                        onValueChange = { dependents = it },
                        label = { Text("Số người phụ thuộc") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = { Icon(Icons.Default.Groups, null, tint = PrimaryBlue) }
                    )
                }
            }
        }

        // Action Buttons
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
            Spacer(Modifier.width(12.dp))
            Text(
                if (editingIncome != null) "CẬP NHẬT THÔNG TIN" else "TÍNH TOÁN & LƯU LẠI",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        // Mock Button (Restored and styled)
        TextButton(
            onClick = {
                val isListenerEnabled = Settings.Secure.getString(context.contentResolver, "enabled_notification_listeners")?.contains(context.packageName) == true
                if (!isListenerEnabled) {
                    context.startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
                } else {
                    val inputAmount = amount.toDoubleOrNull() ?: 5000000.0
                    val inputDeps = dependents.toIntOrNull() ?: 0
                    val taxEngine = com.example.dncuik.tax.TaxEngine()
                    val taxResult = taxEngine.calculate(inputAmount, com.example.dncuik.tax.TaxProfile(), inputDeps, type)
                    
                    val locale = java.util.Locale.getDefault()
                    val formattedAmount = String.format(locale, "%,.0f", inputAmount)
                    val formattedNet = String.format(locale, "%,.0f", taxResult.net)
                    val mockBalance = String.format(locale, "%,.0f", 50000000.0 + taxResult.net)
                    
                    val testText = "VCB +$formattedAmount. Thuc nhan: +$formattedNet. So du: $mockBalance VND"

                    val notification = NotificationCompat.Builder(context, "BANK_ALERTS")
                        .setSmallIcon(android.R.drawable.ic_dialog_info)
                        .setContentTitle("Vietcombank (Mô phỏng)")
                        .setContentText(testText)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setDefaults(NotificationCompat.DEFAULT_ALL)
                        .setAutoCancel(true)
                        .build()
                    
                    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.notify(999, notification)
                }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Icon(Icons.Default.NotificationsActive, null, modifier = Modifier.size(18.dp), tint = PrimaryBlue)
            Spacer(Modifier.width(8.dp))
            Text("Thử nghiệm thông báo ngân hàng", color = PrimaryBlue, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun CurrencyInfoBox(title: String, value: String, icon: ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(title, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(4.dp))
            Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
        }
    }
}
