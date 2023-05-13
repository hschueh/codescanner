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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gonnaggstudio.codescanner.HomeViewModel
import com.gonnaggstudio.codescanner.ui.scan.ScannerCompose
import com.gonnaggstudio.codescanner.ui.utils.hiltActivityViewModel
import com.google.mlkit.vision.barcode.common.Barcode
import kotlinx.coroutines.launch

@Composable
fun MainScreen() {
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
                composable("home") { HomeScreen() }
                composable("history") { HistoryScreen() }
            }
        }
    )
}

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
