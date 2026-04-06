package com.example.laptrinhdidong.ui.charts

import android.graphics.Color
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.laptrinhdidong.data.IncomeEntity
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.formatter.PercentFormatter

import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MonthlyIncomeBarChart(history: List<IncomeEntity>) {
    if (history.isEmpty()) return

    val calendar = Calendar.getInstance()
    val monthlyData = history.groupBy {
        calendar.timeInMillis = it.timestamp
        "${calendar.get(Calendar.MONTH) + 1}/${calendar.get(Calendar.YEAR)}"
    }.mapValues { it.value.sumOf { income -> income.amount } }
    
    val sortedMonths = monthlyData.keys.sortedBy { 
        val parts = it.split("/")
        parts[1].toInt() * 100 + parts[0].toInt()
    }.takeLast(6)

    AndroidView(
        factory = { context ->
            BarChart(context).apply {
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 300)
                description.isEnabled = false
                setFitBars(true)
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.setDrawGridLines(false)
                axisRight.isEnabled = false
            }
        },
        modifier = Modifier.fillMaxWidth().height(250.dp).padding(16.dp),
        update = { chart ->
            val entries = sortedMonths.mapIndexed { index, month ->
                BarEntry(index.toFloat(), monthlyData[month]?.toFloat() ?: 0f)
            }

            val dataSet = BarDataSet(entries, "Thu nhập theo tháng").apply {
                color = Color.parseColor("#1976D2")
                valueTextSize = 10f
            }

            chart.data = BarData(dataSet)
            chart.xAxis.valueFormatter = IndexAxisValueFormatter(sortedMonths)
            chart.xAxis.labelCount = sortedMonths.size
            chart.invalidate()
        }
    )
}

@Composable
fun TaxVsNetChart(history: List<IncomeEntity>) {
    if (history.isEmpty()) return

    val totalTax = history.sumOf { it.taxAmount }
    val totalNet = history.sumOf { it.netAmount }

    AndroidView(
        factory = { context ->
            PieChart(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    300
                )
                description.isEnabled = false
                setUsePercentValues(true)
                holeRadius = 45f
                transparentCircleRadius = 50f
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(16.dp),
        update = { chart ->
            val entries = listOf(
                PieEntry(totalTax.toFloat(), "Tổng Thuế"),
                PieEntry(totalNet.toFloat(), "Thực nhận")
            )

            val dataSet = PieDataSet(entries, "Thuế vs Thực nhận").apply {
                colors = listOf(
                    Color.parseColor("#D32F2F"), // ErrorRed
                    Color.parseColor("#2E7D32")  // SuccessGreen
                )
                valueTextSize = 12f
                valueFormatter = PercentFormatter(chart)
            }

            chart.data = PieData(dataSet)
            chart.invalidate()
        }
    )
}
