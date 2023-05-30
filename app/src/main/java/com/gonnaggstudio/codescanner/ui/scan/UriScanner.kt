package com.gonnaggstudio.codescanner.ui.scan

import android.net.Uri
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.io.IOException

@Composable
fun UriScanner(imageUri: Uri?, onCodeRead: (List<Barcode>) -> Unit, onUriDetectionFinish: () -> Unit) {
    val localContext = LocalContext.current
    val options = remember {
        BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
    }
    val barcodeScanner = remember {
        BarcodeScanning.getClient(options)
    }
    LaunchedEffect(imageUri) {
        if (imageUri == null) return@LaunchedEffect
        try {
            barcodeScanner.process(InputImage.fromFilePath(localContext, imageUri))
                .addOnSuccessListener { barcodes ->
                    onCodeRead(barcodes)
                }
                .addOnFailureListener {
                    it.printStackTrace()
                }
                .addOnCompleteListener {
                    onUriDetectionFinish.invoke()
                }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
