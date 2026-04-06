package com.example.dncuik.ui.camera

import android.view.ViewGroup
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import java.util.concurrent.Executors

@Composable
fun CameraScreen(onResult: (Double, String, String) -> Unit, onClose: () -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var detectedInvoice by remember { mutableStateOf(DetectedInvoice()) }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            },
            modifier = Modifier.fillMaxSize(),
            update = { previewView ->
                val executor = Executors.newSingleThreadExecutor()
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = androidx.camera.core.Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                    val analyzer = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                        .also {
                            it.setAnalyzer(executor, TextRecognitionAnalyzer { invoice ->
                                detectedInvoice = invoice
                            })
                        }

                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            CameraSelector.DEFAULT_BACK_CAMERA,
                            preview,
                            analyzer
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }, ContextCompat.getMainExecutor(context))
            }
        )

        // Overlay giao diện
        Column(
            modifier = Modifier.align(Alignment.BottomCenter).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val amount = detectedInvoice.amount ?: 0.0
            if (amount > 0) {
                Card(colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))) {
                    Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        detectedInvoice.vendor?.let {
                            Text("Đơn vị: $it", style = MaterialTheme.typography.bodyMedium)
                        }
                        detectedInvoice.date?.let {
                            Text("Ngày: $it", style = MaterialTheme.typography.bodySmall)
                        }
                        Text("Phát hiện số tiền:", style = MaterialTheme.typography.labelMedium)
                        Text(String.format("%,.0f VND", amount), style = MaterialTheme.typography.headlineMedium, color = Color(0xFF1976D2))
                        
                        Button(onClick = { 
                            onResult(
                                amount, 
                                detectedInvoice.vendor ?: "Khoản chi từ Camera",
                                detectedInvoice.date ?: ""
                            ) 
                        }) {
                            Text("SỬ DỤNG DỮ LIỆU NÀY")
                        }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))

            IconButton(
                onClick = onClose,
                modifier = Modifier.size(48.dp),
                colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Red, contentColor = Color.White)
            ) {
                Icon(Icons.Default.Close, null)
            }
        }
    }
}
