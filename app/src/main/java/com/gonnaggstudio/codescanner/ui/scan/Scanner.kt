package com.gonnaggstudio.codescanner.ui.scan

import android.content.Context
import android.view.ViewGroup
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import java.util.concurrent.Executor

@Composable
fun ScannerCompose(
    modifier: Modifier,
    onCodeRead: (barcodes: List<Barcode>) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    var cameraIsInitialized: Boolean by remember { mutableStateOf(false) }
    AndroidView(
        modifier = modifier,
        factory = { context ->
            PreviewView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                val options = BarcodeScannerOptions.Builder()
                    .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                    .build()
                val barcodeScanner = BarcodeScanning.getClient(options)

                val lifecycleCameraController = LifecycleCameraController(context).apply {
                    initializationFuture.addListener(
                        { cameraIsInitialized = true },
                        context.executor
                    )
                    setImageAnalysisAnalyzer(
                        context.executor,
                        MlKitAnalyzer(
                            listOf(barcodeScanner),
                            CameraController.COORDINATE_SYSTEM_VIEW_REFERENCED,
                            context.executor
                        ) { result ->
                            result.getValue(barcodeScanner)?.let { barcode ->
                                if (barcode.isNotEmpty()) {
                                    onCodeRead(barcode)
                                }
                            }
                        }
                    )
                    bindToLifecycle(lifecycleOwner)
                }
                controller = lifecycleCameraController
            }
        },
        update = { preview ->
            if (cameraIsInitialized) {
                preview.implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            }
        }
    )
}

private val Context.executor: Executor
    get() = ContextCompat.getMainExecutor(this)
