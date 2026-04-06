package com.example.dncuik.ui.camera

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

data class DetectedInvoice(
    val amount: Double? = null,
    val date: String? = null,
    val vendor: String? = null
)

class TextRecognitionAnalyzer(
    private val onInvoiceDetected: (DetectedInvoice) -> Unit
) : ImageAnalysis.Analyzer {

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    var detectedAmount: Double? = null
                    var detectedDate: String? = null
                    var detectedVendor: String? = null

                    val dateRegex = """\d{1,2}[/-]\d{1,2}[/-]\d{2,4}""".toRegex()
                    val vendorKeywords = listOf("CÔNG TY", "TNHH", "SHOP", "STORE", "MART", "COFFEE")

                    for (block in visionText.textBlocks) {
                        val blockText = block.text.uppercase()
                        
                        // Detect Vendor (heuristic: contains keywords and is usually at the top)
                        if (detectedVendor == null) {
                            for (keyword in vendorKeywords) {
                                if (blockText.contains(keyword)) {
                                    detectedVendor = block.text.split("\n").firstOrNull()
                                    break
                                }
                            }
                        }

                        for (line in block.lines) {
                            val lineText = line.text
                            
                            // Detect Date
                            if (detectedDate == null) {
                                val dateMatch = dateRegex.find(lineText)
                                if (dateMatch != null) {
                                    detectedDate = dateMatch.value
                                }
                            }

                            // Detect Amount
                            val cleanText = lineText.replace(".", "").replace(",", "")
                            val amount = cleanText.filter { it.isDigit() }.toDoubleOrNull()
                            if (amount != null && amount >= 10000) {
                                // Prefer larger amounts as total (if multiple found)
                                val currentAmount = detectedAmount
                                if (currentAmount == null || amount > currentAmount) {
                                    detectedAmount = amount
                                }
                            }
                        }
                    }

                    if (detectedAmount != null || detectedDate != null || detectedVendor != null) {
                        onInvoiceDetected(DetectedInvoice(detectedAmount, detectedDate, detectedVendor))
                    }
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }
}
