package com.gonnaggstudio.codescanner.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gonnaggstudio.codescanner.db.dao.BarcodeDao
import com.gonnaggstudio.codescanner.pref.DatastoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    val datastoreManager: DatastoreManager,
    val barcodeDao: BarcodeDao
) : ViewModel() {

    val uiState: StateFlow<UiState> = datastoreManager.readBooleans(*KEYS_IN_DATASTORE).map { preferences ->
        UiState(
            settingItems = SETTING_ITEMS.map { settingItem ->
                when (settingItem) {
                    is SettingItem.ClickableItem -> settingItem
                    is SettingItem.SwitchItem -> settingItem.copy(
                        isEnabled = preferences[settingItem.enumKey.key] ?: false
                    )
                }
            }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = UiState()
    )

    fun onAction(uiAction: UiAction) {
        when (uiAction) {
            is UiAction.OnSettingItemClicked -> {
                onSettingItemClicked(uiAction.key)
            }
        }
    }

    private fun onSettingItemClicked(key: SettingItemKey) {
        when (key) {
            SettingItemKey.CLEAR_HISTORY -> {
                viewModelScope.launch(Dispatchers.IO) {
                    barcodeDao.deleteAll()
                }
            }
            SettingItemKey.OPEN_WEB_IN_INCOGNITO -> {
                viewModelScope.launch(Dispatchers.IO) {
                    datastoreManager
                        .readBooleans(SettingItemKey.OPEN_WEB_IN_INCOGNITO.key)
                        .first()[SettingItemKey.OPEN_WEB_IN_INCOGNITO.key]?.let { currentValue ->
                        datastoreManager.saveBoolean(SettingItemKey.OPEN_WEB_IN_INCOGNITO.key, !currentValue)
                    }
                }
            }
            null -> {
                // Do nothing
            }
        }
    }

    sealed class UiAction {
        data class OnSettingItemClicked(val key: SettingItemKey) : UiAction()
    }

    data class UiState(
        val settingItems: List<SettingItem> = emptyList()
    )

    sealed class SettingItem {
        data class ClickableItem(val title: String, val enumKey: SettingItemKey) : SettingItem()
        data class SwitchItem(val title: String, val enumKey: SettingItemKey, val isEnabled: Boolean) : SettingItem()
    }

    enum class SettingItemKey(val key: String) {
        CLEAR_HISTORY("clear_history"),
        OPEN_WEB_IN_INCOGNITO("open_web_in_incognito")
    }

    companion object {
        private val SETTING_ITEMS = listOf(
            SettingItem.ClickableItem("Clear History", SettingItemKey.CLEAR_HISTORY),
            SettingItem.SwitchItem("Open web in incognito", SettingItemKey.OPEN_WEB_IN_INCOGNITO, false),
        )

        private val KEYS_IN_DATASTORE = arrayOf(SettingItemKey.OPEN_WEB_IN_INCOGNITO.key)
    }
}
