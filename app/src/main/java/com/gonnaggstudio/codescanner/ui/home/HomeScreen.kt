package com.gonnaggstudio.codescanner.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gonnaggstudio.codescanner.MainViewModel
import com.gonnaggstudio.codescanner.R
import com.gonnaggstudio.codescanner.ext.toBarcode
import com.gonnaggstudio.codescanner.ui.scan.ScannerCompose
import com.gonnaggstudio.codescanner.ui.scan.ScannerOverlay
import com.gonnaggstudio.codescanner.ui.utils.hiltActivityViewModel
import com.google.mlkit.vision.barcode.common.Barcode

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    mainViewModel: MainViewModel = hiltActivityViewModel()
) {
    val state: HomeViewModel.UiState by homeViewModel.uiState.collectAsState()
    when (val fixedState = state) {
        is HomeViewModel.UiState.Scanning -> {
            HomeScreenScanning(
                state = fixedState,
                onBarcodeReceived = { homeViewModel.onAction(HomeViewModel.UiAction.OnBarcodeReceived(it)) },
                onBarcodeClicked = {
                    mainViewModel.onAction(MainViewModel.UiAction.OpenBarcodeLink(it.toBarcode()))
                },
                onTorchClicked = {
                    homeViewModel.onAction(HomeViewModel.UiAction.OnTorchClicked)
                }
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
    onBarcodeClicked: (Barcode) -> Unit = {},
    onTorchClicked: () -> Unit = {}
) {
    Box {
        ScannerCompose(
            modifier = Modifier.fillMaxSize(),
            torchEnabled = state.isTorchEnabled
        ) {
            onBarcodeReceived(it)
        }
        ScannerOverlay(
            modifier = Modifier.fillMaxSize()
        )
        HomeScreenFooter(
            modifier = Modifier
                .wrapContentHeight()
                .align(Alignment.BottomCenter)
                .padding(bottom = 72.dp),
            barcode = state.list.firstOrNull(),
            onBarcodeClicked = onBarcodeClicked,
            isTorchEnabled = state.isTorchEnabled,
            onTorchClicked = onTorchClicked
        )
    }
}

@Composable
fun HomeScreenFooter(
    modifier: Modifier = Modifier,
    barcode: Barcode? = null,
    onBarcodeClicked: (Barcode) -> Unit = {},
    isTorchEnabled: Boolean = false,
    onTorchClicked: () -> Unit = {},
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        barcode?.let {
            Text(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .background(
                        color = Color.LightGray,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .clickable {
                        onBarcodeClicked(barcode)
                    }
                    .wrapContentHeight()
                    .padding(12.dp),
                text = barcode.rawValue ?: "Null",
                style = MaterialTheme.typography.body1
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            IconButton(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.LightGray.copy(0.8f), shape = RoundedCornerShape(24.dp)),
                onClick = onTorchClicked
            ) {
                Icon(
                    imageVector = when (isTorchEnabled) {
                        true -> {
                            ImageVector.vectorResource(id = R.drawable.ic_flash_on)
                        }
                        false -> {
                            ImageVector.vectorResource(id = R.drawable.ic_flash_off)
                        }
                    },
                    contentDescription = "isTorchEnabled = $isTorchEnabled",
                    tint = Color.DarkGray
                )
            }
        }
    }
}
