package com.gonnaggstudio.codescanner.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gonnaggstudio.codescanner.HomeViewModel
import com.gonnaggstudio.codescanner.ui.scan.ScannerCompose
import com.gonnaggstudio.codescanner.web.CustomTabUtils
import com.google.mlkit.vision.barcode.common.Barcode
import dagger.hilt.android.internal.migration.InjectedByHilt
import kotlinx.coroutines.launch
import javax.inject.Inject

@Composable
fun MainScreen(
    openUrl: (Barcode) -> Unit = {}
) {
    val navController = rememberNavController()
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        scaffoldState = scaffoldState,
        drawerContent = {
            Text("drawer content")
            Text("drawer content")
            Text("drawer content")
        },
        topBar = {
            TopAppBar(
                title = {
                    Text("QR Code Reader")
                },
                actions = {
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                if (scaffoldState.drawerState.isOpen) {
                                    scaffoldState.drawerState.close()
                                } else {
                                    scaffoldState.drawerState.open()
                                }
                            }
                        }
                    ) {
                        Icon(Icons.Default.Menu, "Toggle Drawer")
                    }
                }
            )
        },
        content = {
            NavHost(navController = navController, "home") {
                composable("home") { HomeScreen(openUrl) }
                composable("history") { HistoryScreen() }
            }
        }
    )
}

@Composable
fun HomeScreen(
    openUrl: (Barcode) -> Unit,
    homeViewModel: HomeViewModel = hiltViewModel(),
) {
    val state: HomeViewModel.UiState by homeViewModel.uiState.collectAsState()
    when (state) {
        HomeViewModel.UiState.PermissionDenied -> {
        }
        is HomeViewModel.UiState.Scanning -> {
            HomeScreenScanning(
                state = state as HomeViewModel.UiState.Scanning,
                onBarcodeReceived = { homeViewModel.onAction(HomeViewModel.UiAction.OnBarcodeReceived(it)) },
                onBarcodeClicked = {
                    openUrl(it)
                    // TODO: use action and event to be neater. Also we can keep clicked record in our database.
                    // homeViewModel.onAction(HomeViewModel.UiAction.OnBarcodeClicked(it))
                },
            )
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
                modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                state.list.map { barcode ->
                    Text(
                        modifier = Modifier
                            .padding(4.dp)
                            .background(
                                color = Color.DarkGray,
                                shape = RoundedCornerShape(4.dp)
                            ).clickable {
                                onBarcodeClicked(barcode)
                            }.padding(4.dp),
                        text = barcode.rawValue ?: "Null",
                    )
                }
            }
        }
    }
}

@Composable
fun HistoryScreen() {
    Text("QR Code Screen")
}

@Preview
@Composable
fun MainScreenPreview() {
    MainScreen()
}
