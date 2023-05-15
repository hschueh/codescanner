package com.gonnaggstudio.codescanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gonnaggstudio.codescanner.db.dao.BarcodeDao
import com.gonnaggstudio.codescanner.ext.toEntity
import com.gonnaggstudio.codescanner.model.Barcode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var barcodeDao: BarcodeDao

    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Default)
    val uiState: StateFlow<UiState> = _uiState
    private val _uiEvent: MutableStateFlow<UiEvent> = MutableStateFlow(UiEvent())
    val uiEvent: StateFlow<UiEvent> = _uiEvent

    fun onAction(uiAction: UiAction) {
        when (uiAction) {
            UiAction.BackToHomePage -> _uiState.value = UiState.Home
            UiAction.GoToHistoryPage -> _uiState.value = UiState.History
            UiAction.GoToSettingsPage -> _uiState.value = UiState.Settings
            UiAction.NavFinished -> _uiState.value = UiState.Default
            is UiAction.GoToDetailPage -> _uiState.value = UiState.Detail(uiAction.barcodeId)
            UiAction.BarcodeDetailPageOpened -> {
                onBarcodeDetailPageOpened()
            }
            UiAction.LinkOpened -> {
                onLinkOpened()
            }
            is UiAction.OpenUrlLink -> {
                openUrlLink(uiAction.url)
            }
            is UiAction.ViewBarcodeDetail -> {
                navigateToBarcodeDetail(uiAction.barcode)
            }
            UiAction.BarcodeOpened -> {
                onBarcodeOpened()
            }
            is UiAction.OpenBarcodeLink -> {
                openBarcode(uiAction.barcode)
            }
        }
    }

    private fun openBarcode(barcode: Barcode) {
        if (barcode == uiEvent.value.barcodeToOpen) return
        _uiEvent.value = uiEvent.value.copy(
            barcodeToOpen = barcode
        )
        viewModelScope.launch(Dispatchers.IO) {
            barcodeDao.insert(barcode.toEntity())
        }
    }

    private fun onBarcodeOpened() {
        _uiEvent.value = uiEvent.value.copy(
            barcodeToOpen = null
        )
    }

    private fun navigateToBarcodeDetail(barcode: Barcode) {
        val event = uiEvent.value
        if (barcode == event.barcodeToViewDetail) return
        _uiEvent.value = event.copy(
            barcodeToViewDetail = barcode
        )
    }

    private fun onBarcodeDetailPageOpened() {
        val event = uiEvent.value
        if (null == event.barcodeToViewDetail) return
        _uiEvent.value = event.copy(
            barcodeToViewDetail = null
        )
    }

    private fun openUrlLink(url: String) {
        val state = uiEvent.value
        if (url == state.linkToOpen) return
        _uiEvent.value = state.copy(
            linkToOpen = url
        )
        /* TODO: find and update lastInteractAt
        viewModelScope.launch(Dispatchers.IO) {
        }
         */
    }

    private fun onLinkOpened() {
        _uiEvent.value = uiEvent.value.copy(
            linkToOpen = null
        )
    }

    sealed class UiAction {
        object BackToHomePage : UiAction()
        object GoToHistoryPage : UiAction()
        object GoToSettingsPage : UiAction()
        data class GoToDetailPage(val barcodeId: Int) : UiAction()
        object NavFinished : UiAction()
        data class OpenBarcodeLink(val barcode: Barcode) : UiAction()
        data class OpenUrlLink(val url: String) : UiAction()
        data class ViewBarcodeDetail(val barcode: Barcode) : UiAction()
        object LinkOpened : UiAction()
        object BarcodeOpened : UiAction()
        object BarcodeDetailPageOpened : UiAction()
    }

    sealed class UiState {
        object Default : UiState()
        object Home : UiState()
        object History : UiState()
        object Settings : UiState()
        data class Detail(val barcodeId: Int) : UiState()
    }

    data class UiEvent(
        val linkToOpen: String? = null,
        val barcodeToOpen: Barcode? = null,
        val barcodeToViewDetail: Barcode? = null
    )

    companion object {
        const val NAVIGATION_HOME = "home"
        const val NAVIGATION_HISTORY = "history"
        const val NAVIGATION_SETTINGS = "settings"
        const val NAVIGATION_DETAIL = "detail"
    }
}
