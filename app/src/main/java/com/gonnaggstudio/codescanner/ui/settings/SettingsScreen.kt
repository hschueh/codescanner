package com.gonnaggstudio.codescanner.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val state: SettingsViewModel.UiState by settingsViewModel.uiState.collectAsState()

    Column {
        state.settingItems.map {
            when (it) {
                is SettingsViewModel.SettingItem.ClickableItem -> {
                    SettingsClickable(
                        label = it.title,
                        onClick = {
                            settingsViewModel.onAction(SettingsViewModel.UiAction.OnSettingItemClicked(it.key))
                        }
                    )
                }
                is SettingsViewModel.SettingItem.SwitchItem -> {
                    SettingsSwitchable(
                        label = it.title,
                        isChecked = it.isEnabled,
                        onClick = {
                            settingsViewModel.onAction(SettingsViewModel.UiAction.OnSettingItemClicked(it.key))
                        }
                    )
                }
            }
            Divider(Modifier.fillMaxWidth(), thickness = 1.dp, color = Color.LightGray)
        }
    }
}

@Composable
fun SettingsClickable(label: String, onClick: () -> Unit) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .clickable {
            onClick.invoke()
        }
        .padding(16.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.body1
        )
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun SettingsSwitchable(label: String, isChecked: Boolean, onClick: () -> Unit) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .clickable {
            onClick.invoke()
        }
        .padding(16.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.body1
        )
        Spacer(modifier = Modifier.weight(1f))
        Switch(
            checked = isChecked,
            onCheckedChange = null
        )
    }
}
