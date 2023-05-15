package com.gonnaggstudio.codescanner

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Default)
    val uiState: StateFlow<UiState> = _uiState

    fun onAction(uiAction: UiAction) {
        when (uiAction) {
            UiAction.BackToHomePage -> _uiState.value = UiState.Home
            UiAction.GoToHistoryPage -> _uiState.value = UiState.History
            UiAction.GoToSettingsPage -> _uiState.value = UiState.Settings
            UiAction.NavFinished -> _uiState.value = UiState.Default
            is UiAction.GoToDetailPage -> _uiState.value = UiState.Detail(uiAction.barcodeId)
        }
    }

    sealed class UiAction {
        object BackToHomePage : UiAction()
        object GoToHistoryPage : UiAction()
        object GoToSettingsPage : UiAction()
        data class GoToDetailPage(val barcodeId: Int) : UiAction()
        object NavFinished : UiAction()
    }

    sealed class UiState {
        object Default : UiState()
        object Home : UiState()
        object History : UiState()
        object Settings : UiState()
        data class Detail(val barcodeId: Int) : UiState()
    }

    companion object {
        const val NAVIGATION_HOME = "home"
        const val NAVIGATION_HISTORY = "history"
        const val NAVIGATION_SETTINGS = "settings"
        const val NAVIGATION_DETAIL = "detail"
    }
}
