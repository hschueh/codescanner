package com.gonnaggstudio.codescanner.ui.scan

import android.content.Context
import android.view.ViewGroup
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import java.util.concurrent.Executor

@Composable
fun ScannerCompose(
    modifier: Modifier,
    torchEnabled: Boolean = false,
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
                torchEnabled.let {
                    preview.controller?.enableTorch(it)
                }
            }
        }
    )
}

@Composable
fun ScannerOverlay(modifier: Modifier = Modifier) {
    val configuration = LocalConfiguration.current
    var viewSize by remember { mutableStateOf(IntSize.Zero) }
    val width = viewSize.width * 0.8f
    val offsetX = (viewSize.width - width) * 0.5f
    val offsetY = (viewSize.height - width) * 0.4f
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { newSize ->
                viewSize = newSize
            }
    ) {
        clipRect(
            left = offsetX,
            top = offsetY,
            right = (offsetX + width),
            bottom = (offsetY + width),
            clipOp = ClipOp.Difference
        ) {
            drawRect(
                color = Color.LightGray.copy(alpha = 0.4f),
                topLeft = Offset(0f, 0f),
                size = Size(
                    configuration.screenWidthDp.dp.toPx(),
                    configuration.screenHeightDp.dp.toPx()
                )
            )
        }
    }
}

private val Context.executor: Executor
    get() = ContextCompat.getMainExecutor(this)
