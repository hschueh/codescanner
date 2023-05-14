package com.gonnaggstudio.codescanner.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.gonnaggstudio.codescanner.ui.scan.ScannerCompose
import com.gonnaggstudio.codescanner.ui.utils.hiltActivityViewModel
import com.google.mlkit.vision.barcode.common.Barcode

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltActivityViewModel(),
) {
    val state: HomeViewModel.UiState by homeViewModel.uiState.collectAsState()
    when (val fixedState = state) {
        is HomeViewModel.UiState.Scanning -> {
            HomeScreenScanning(
                state = fixedState,
                onBarcodeReceived = { homeViewModel.onAction(HomeViewModel.UiAction.OnBarcodeReceived(it)) },
                onBarcodeClicked = {
                    homeViewModel.onAction(HomeViewModel.UiAction.OnBarcodeClicked(it))
                },
            )
        }
        is HomeViewModel.UiState.PermissionDenied -> {
        }
    }
}

@Composable
fun HomeScreenScanning(
    state: HomeViewModel.UiState.Scanning,
    onBarcodeReceived: (List<Barcode>) -> Unit,
    onBarcodeClicked: (Barcode) -> Unit = {}
) {
    Box {
        ScannerCompose(
            modifier = Modifier.fillMaxSize()
        ) {
            onBarcodeReceived(it)
        }
        if (state.list.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                state.list.map { barcode ->
                    Text(
                        modifier = Modifier
                            .padding(4.dp)
                            .background(
                                color = Color.DarkGray,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .clickable {
                                onBarcodeClicked(barcode)
                            }
                            .padding(4.dp),
                        text = barcode.rawValue ?: "Null",
                    )
                }
            }
        }
    }
}
