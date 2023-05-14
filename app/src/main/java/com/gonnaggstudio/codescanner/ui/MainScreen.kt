package com.gonnaggstudio.codescanner.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gonnaggstudio.codescanner.MainViewModel
import com.gonnaggstudio.codescanner.ui.history.HistoryScreen
import com.gonnaggstudio.codescanner.ui.home.HomeScreen
import com.gonnaggstudio.codescanner.ui.menu.DrawerMenu
import com.gonnaggstudio.codescanner.ui.settings.SettingsScreen
import com.gonnaggstudio.codescanner.ui.utils.hiltActivityViewModel
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    mainViewModel: MainViewModel = hiltActivityViewModel()
) {
    val navController = rememberNavController()
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    val mainState: MainViewModel.UiState by mainViewModel.uiState.collectAsState()

    Scaffold(
        scaffoldState = scaffoldState,
        drawerContent = { DrawerMenu(scaffoldState) },
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
                composable(MainViewModel.NAVIGATION_HOME) { HomeScreen() }
                composable(MainViewModel.NAVIGATION_HISTORY) { HistoryScreen() }
                composable(MainViewModel.NAVIGATION_SETTINGS) { SettingsScreen() }
            }
        }
    )

    // TODO: Not sure where should I put this to avoid NPE. Add default state to avoid nav before everything's settled.
    when (mainState) {
        MainViewModel.UiState.Home -> {
            navController.navigate(MainViewModel.NAVIGATION_HOME)
        }
        MainViewModel.UiState.History -> {
            navController.navigate(MainViewModel.NAVIGATION_HISTORY)
        }
        MainViewModel.UiState.Detail -> {
        }
        MainViewModel.UiState.Settings -> {
            navController.navigate(MainViewModel.NAVIGATION_SETTINGS)
        }
        else -> {}
    }
}

@Preview
@Composable
fun MainScreenPreview() {
    MainScreen()
}
