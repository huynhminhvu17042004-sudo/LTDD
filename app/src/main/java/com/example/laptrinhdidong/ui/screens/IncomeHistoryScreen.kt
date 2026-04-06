package com.example.laptrinhdidong.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.laptrinhdidong.ui.theme.ErrorRed
import com.example.laptrinhdidong.ui.theme.PrimaryBlue
import com.example.laptrinhdidong.ui.theme.SuccessGreen
import com.example.laptrinhdidong.data.IncomeEntity
import com.example.laptrinhdidong.ui.charts.MonthlyIncomeBarChart
import com.example.laptrinhdidong.ui.charts.TaxVsNetChart
import com.example.laptrinhdidong.ui.viewmodel.MainViewModel
import java.util.Locale

@Composable
fun IncomeHistoryScreen(viewModel: MainViewModel) {
    val history by viewModel.incomeHistory.collectAsState()
    val context = LocalContext.current

    Scaffold(
        containerColor = Color.Transparent,
        floatingActionButton = {
            if (history.isNotEmpty()) {
                ExtendedFloatingActionButton(
                    onClick = { viewModel.exportToExcel(context) },
                    icon = { Icon(Icons.Default.Description, null) },
                    text = { Text("Xuất báo cáo Excel") },
                    containerColor = SuccessGreen,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp)
                )
            }
        }
    ) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)) {
            item {
                Spacer(Modifier.height(16.dp))
                Text("Lịch Sử Giao Dịch", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                Text("Quản lý các nguồn thu nhập đã lưu", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                Spacer(Modifier.height(16.dp))
            }
            items(history) { item ->
                HistoryItem(
                    item = item, 
                    onEdit = { viewModel.setEditingIncome(item) },
                    onDelete = { viewModel.deleteIncome(item) }
                )
            }
            if (history.isNotEmpty()) {
                item {
                    Text(
                        "Xu hướng thu nhập (6 tháng)", 
                        style = MaterialTheme.typography.titleMedium, 
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                    MonthlyIncomeBarChart(history)

                    Text(
                        "Tỉ lệ Thuế và Thực nhận", 
                        style = MaterialTheme.typography.titleMedium, 
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                    TaxVsNetChart(history)
                }
            }
            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun HistoryItem(item: IncomeEntity, onEdit: () -> Unit, onDelete: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    val locale = Locale.getDefault()

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Xác nhận xóa") },
            text = { Text("Dữ liệu này sẽ bị xóa vĩnh viễn khỏi lịch sử.") },
            confirmButton = {
                TextButton(onClick = { onDelete(); showDeleteConfirm = false }) {
                    Text("XÓA", color = ErrorRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("QUAY LẠI")
                }
            }
        )
    }

    Card(
        onClick = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(if (expanded) 6.dp else 2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(item.sourceName, fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleMedium)
                    Text(
                        when(item.sourceType) {
                            "SALARY" -> "Lương nhân viên"
                            "RENTAL" -> "Cho thuê tài sản"
                            "INVESTMENT" -> "Đầu tư tài chính"
                            else -> item.sourceType
                        }, 
                        style = MaterialTheme.typography.labelSmall,
                        color = PrimaryBlue
                    )
                }
                Text(
                    String.format(locale, "%,.0fđ", item.netAmount), 
                    color = SuccessGreen, 
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp
                )
            }
            
            if (expanded) {
                Divider(Modifier.padding(vertical = 12.dp), thickness = 0.5.dp, color = Color.LightGray)
                DetailRow("Gross:", String.format(locale, "%,.0f VND", item.amount))
                if (item.sourceType == "SALARY") {
                    DetailRow("Bảo hiểm:", String.format(locale, "-%,.0f VND", item.insuranceAmount), ErrorRed)
                    DetailRow("Người phụ thuộc:", "${item.dependents}")
                }
                DetailRow("Thuế TNCN:", String.format(locale, "-%,.0f VND", item.taxAmount), ErrorRed)
                
                Row(Modifier.fillMaxWidth().padding(top = 12.dp), horizontalArrangement = Arrangement.End) {
                    IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, "Sửa", tint = PrimaryBlue) }
                    IconButton(onClick = { showDeleteConfirm = true }) { Icon(Icons.Default.Delete, "Xóa", tint = ErrorRed) }
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String, color: Color = Color.Unspecified) {
    Row(Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodySmall)
        Text(value, style = MaterialTheme.typography.bodySmall, color = color, fontWeight = FontWeight.Bold)
    }
}
