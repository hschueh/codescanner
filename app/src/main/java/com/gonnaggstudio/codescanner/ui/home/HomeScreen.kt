package com.gonnaggstudio.codescanner.ui.home

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gonnaggstudio.codescanner.MainViewModel
import com.gonnaggstudio.codescanner.R
import com.gonnaggstudio.codescanner.ext.toBarcode
import com.gonnaggstudio.codescanner.ui.scan.ScannerCompose
import com.gonnaggstudio.codescanner.ui.scan.ScannerOverlay
import com.gonnaggstudio.codescanner.ui.utils.hiltActivityViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionRequired
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.barcode.common.Barcode

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    mainViewModel: MainViewModel = hiltActivityViewModel()
) {
    val permissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)

    LaunchedEffect(true) {
        permissionState.launchPermissionRequest()
    }

    val state: HomeViewModel.UiState by homeViewModel.uiState.collectAsState()
    PermissionRequired(
        permissionState = permissionState,
        permissionNotGrantedContent = { HomePermissionRequest(permissionState) },
        permissionNotAvailableContent = { HomePermissionRequest(permissionState) }
    ) {
        HomeScreenScanning(
            state = state as HomeViewModel.UiState.Scanning,
            onBarcodeReceived = {
                homeViewModel.onAction(
                    HomeViewModel.UiAction.OnBarcodeReceived(it)
                )
            },
            onBarcodeClicked = {
                mainViewModel.onAction(MainViewModel.UiAction.OpenBarcodeLink(it.toBarcode()))
            },
            onTorchClicked = {
                homeViewModel.onAction(HomeViewModel.UiAction.OnTorchClicked)
            }
        )
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

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomePermissionRequest(
    permissionState: PermissionState
) {
    val context = LocalContext.current
    val textToShow = if (permissionState.shouldShowRationale) {
        stringResource(R.string.the_camera_is_important)
    } else {
        stringResource(R.string.camera_permission_required_for)
    }
    val textOnButton = if (permissionState.shouldShowRationale) {
        stringResource(R.string.request_permission)
    } else {
        stringResource(R.string.open_settings)
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = textToShow,
            style = MaterialTheme.typography.body1,
            textAlign = TextAlign.Center,
        )
        Button(
            onClick = {
                if (permissionState.shouldShowRationale) {
                    permissionState.launchPermissionRequest()
                } else {
                    context.startActivity(
                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                    )
                }
            }
        ) { Text(text = textOnButton) }
    }
}
