package com.example.laptrinhdidong

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.laptrinhdidong.ui.screens.IncomeHistoryScreen
import com.example.laptrinhdidong.ui.screens.TaxCalculatorScreen
import com.example.laptrinhdidong.ui.theme.DựÁnCuốiKìTheme
import com.example.laptrinhdidong.ui.theme.PrimaryBlue
import com.example.laptrinhdidong.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

val BackgroundGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFFF0F4F8), Color(0xFFD9E2EC))
)

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(
                android.Manifest.permission.POST_NOTIFICATIONS,
                android.Manifest.permission.CAMERA
            ), 101)
        }

        setContent {
            DựÁnCuốiKìTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen(viewModel: MainViewModel = hiltViewModel()) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val editingIncome by viewModel.editingIncome.collectAsState()

    LaunchedEffect(editingIncome) {
        if (editingIncome != null) selectedTab = 0
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Calculate, null) },
                    label = { Text("Tính toán") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryBlue,
                        selectedTextColor = PrimaryBlue
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.History, null) },
                    label = { Text("Lịch sử") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryBlue,
                        selectedTextColor = PrimaryBlue
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundGradient)
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> TaxCalculatorScreen(viewModel)
                1 -> IncomeHistoryScreen(viewModel)
            }
        }
    }
}
