package com.gonnaggstudio.codescanner.ui.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Divider
import androidx.compose.material.ScaffoldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gonnaggstudio.codescanner.MainViewModel
import com.gonnaggstudio.codescanner.R
import com.gonnaggstudio.codescanner.ui.utils.ButtonRow
import com.gonnaggstudio.codescanner.ui.utils.hiltActivityViewModel
import kotlinx.coroutines.launch

@Composable
fun ColumnScope.DrawerMenu(
    scaffoldState: ScaffoldState,
    mainViewModel: MainViewModel = hiltActivityViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    ButtonRow(
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            mainViewModel.onAction(MainViewModel.UiAction.BackToHomePage)
            coroutineScope.launch {
                scaffoldState.drawerState.close()
            }
        },
        icon = Icons.Default.Home,
        label = stringResource(R.string.back_to_homepage)
    )
    Divider(Modifier.fillMaxWidth().height(1.dp).background(Color.Gray))
    ButtonRow(
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            mainViewModel.onAction(MainViewModel.UiAction.GoToHistoryPage)
            coroutineScope.launch {
                scaffoldState.drawerState.close()
            }
        },
        icon = Icons.Default.DateRange,
        label = stringResource(R.string.history)
    )
    Divider(Modifier.fillMaxWidth().height(1.dp).background(Color.Gray))
    ButtonRow(
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            mainViewModel.onAction(MainViewModel.UiAction.GoToSettingsPage)
            coroutineScope.launch {
                scaffoldState.drawerState.close()
            }
        },
        icon = Icons.Default.Settings,
        label = stringResource(R.string.settings)
    )
}
