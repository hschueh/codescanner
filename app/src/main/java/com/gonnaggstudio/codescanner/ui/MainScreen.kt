package com.gonnaggstudio.codescanner.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gonnaggstudio.codescanner.ui.scan.ScannerCompose
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
fun HomeScreen() {
    var url: String? by remember { mutableStateOf(null) }
    Box {
        ScannerCompose(
            modifier = Modifier.fillMaxSize()
        ) {
            url = it.rawValue ?: url
        }
        url?.let {
            Text(
                modifier = Modifier.align(Alignment.BottomCenter),
                text = it
            )
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
