package com.gonnaggstudio.codescanner.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gonnaggstudio.codescanner.MainViewModel
import com.gonnaggstudio.codescanner.R
import com.gonnaggstudio.codescanner.ui.utils.hiltActivityViewModel

@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    mainViewModel: MainViewModel = hiltActivityViewModel()
) {
    val state: SettingsViewModel.UiState by settingsViewModel.uiState.collectAsState()

    Column {
        state.settingItems.map {
            when (it) {
                is SettingsViewModel.SettingItem.ClickableItem -> {
                    SettingsClickable(
                        label = stringResource(id = it.title),
                        onClick = {
                            settingsViewModel.onAction(SettingsViewModel.UiAction.OnSettingItemClicked(it.enumKey))
                        }
                    )
                }
                is SettingsViewModel.SettingItem.SwitchItem -> {
                    SettingsSwitchable(
                        label = stringResource(id = it.title),
                        isChecked = it.isEnabled,
                        onClick = {
                            settingsViewModel.onAction(SettingsViewModel.UiAction.OnSettingItemClicked(it.enumKey))
                        }
                    )
                }
            }
            Divider(Modifier.fillMaxWidth(), thickness = 1.dp, color = Color.LightGray)
        }
        IncognitoGuidance(
            onUrlClick = { url ->
                mainViewModel.onAction(MainViewModel.UiAction.CopyLink(url))
            }
        )
    }
}

@Composable
fun SettingsClickable(label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
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
    Row(
        modifier = Modifier
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

/**
 TODO: Can't open chrome directly in chrome://xxx url :(
 Need to enabled these two flags:
 - chrome://flags/#cct-incognito
 - chrome://flags/#cct-incognito-available-to-third-party
 ref: https://stackoverflow.com/a/72540492
 */

@Composable
fun IncognitoGuidance(
    onUrlClick: (String) -> Unit
) {
    val fullText = stringResource(id = R.string.need_to_enabled_these)
    val cctIncognitoTag = "cct-incognito"
    val cctIncognito = "chrome://flags/#cct-incognito"
    val cctIncognitoStart = fullText.indexOf(cctIncognito)
    val cctIncognitoEnd = cctIncognitoStart + cctIncognito.length
    val cctIncognitoThirdPartyTag = "cct-incognito-available-to-third-party"
    val cctIncognitoThirdParty = "chrome://flags/#cct-incognito-available-to-third-party"
    val cctIncognitoThirdPartyStart = fullText.indexOf(cctIncognitoThirdParty)
    val cctIncognitoThirdPartyEnd = cctIncognitoThirdPartyStart + cctIncognitoThirdParty.length

    val annotatedString = remember {
        AnnotatedString.Builder().apply {
            append(fullText)
            addStringAnnotation(
                tag = cctIncognitoTag,
                annotation = cctIncognito,
                start = cctIncognitoStart,
                end = cctIncognitoEnd
            )
            addStyle(
                style = SpanStyle(
                    color = Color.Blue,
                    fontSize = 12.sp
                ),
                start = cctIncognitoStart,
                end = cctIncognitoEnd
            )
            addStringAnnotation(
                tag = cctIncognitoThirdPartyTag,
                annotation = cctIncognitoThirdParty,
                start = cctIncognitoThirdPartyStart,
                end = cctIncognitoThirdPartyEnd
            )
            addStyle(
                style = SpanStyle(
                    color = Color.Blue,
                    fontSize = 12.sp
                ),
                start = cctIncognitoThirdPartyStart,
                end = cctIncognitoThirdPartyEnd
            )
        }.toAnnotatedString()
    }
    ClickableText(
        modifier = Modifier.padding(16.dp),
        text = annotatedString,
        style = MaterialTheme.typography.body2,
        onClick = {
            annotatedString.getStringAnnotations(cctIncognitoTag, it, it).firstOrNull()?.let {
                onUrlClick(cctIncognito)
            }
            annotatedString.getStringAnnotations(cctIncognitoThirdPartyTag, it, it).firstOrNull()?.let {
                onUrlClick(cctIncognitoThirdParty)
            }
        }
    )
}
