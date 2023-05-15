package com.gonnaggstudio.codescanner.ui

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.gonnaggstudio.codescanner.MainViewModel
import com.gonnaggstudio.codescanner.ui.detail.DetailScreen
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
            NavHost(navController = navController, MainViewModel.NAVIGATION_HOME) {
                composable(MainViewModel.NAVIGATION_HOME) { HomeScreen() }
                composable(MainViewModel.NAVIGATION_HISTORY) { HistoryScreen() }
                composable(MainViewModel.NAVIGATION_SETTINGS) { SettingsScreen() }
                composable(
                    "${MainViewModel.NAVIGATION_DETAIL}/{barcodeId}",
                    arguments = listOf(
                        navArgument("barcodeId") { type = NavType.IntType },
                    ),
                ) {
                    DetailScreen()
                }
            }
        }
    )

    // TODO: Not sure where should I put this to avoid NPE. Add default state to avoid nav before everything's settled.
    when (val state = mainState) {
        MainViewModel.UiState.Home -> {
            if (navController.currentDestination?.route != MainViewModel.NAVIGATION_HOME) {
                navController.navigate(MainViewModel.NAVIGATION_HOME) {
                    popUpTo(MainViewModel.NAVIGATION_HOME) { inclusive = true }
                }
            }
            mainViewModel.onAction(MainViewModel.UiAction.NavFinished)
        }
        MainViewModel.UiState.History -> {
            navController.navigate(MainViewModel.NAVIGATION_HISTORY) {
                popUpTo(MainViewModel.NAVIGATION_HOME)
            }
            mainViewModel.onAction(MainViewModel.UiAction.NavFinished)
        }
        is MainViewModel.UiState.Detail -> {
            navController.navigate("${MainViewModel.NAVIGATION_DETAIL}/${state.barcodeId}")
            mainViewModel.onAction(MainViewModel.UiAction.NavFinished)
        }
        MainViewModel.UiState.Settings -> {
            navController.navigate(MainViewModel.NAVIGATION_SETTINGS) {
                popUpTo(MainViewModel.NAVIGATION_HOME)
            }
            mainViewModel.onAction(MainViewModel.UiAction.NavFinished)
        }
        else -> {}
    }
}

@Preview
@Composable
fun MainScreenPreview() {
    MainScreen()
}
