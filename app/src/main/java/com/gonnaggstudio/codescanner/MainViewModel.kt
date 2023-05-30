package com.gonnaggstudio.codescanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gonnaggstudio.codescanner.db.dao.BarcodeDao
import com.gonnaggstudio.codescanner.ext.toEntity
import com.gonnaggstudio.codescanner.model.Barcode
import com.gonnaggstudio.codescanner.pref.DatastoreManager
import com.gonnaggstudio.codescanner.ui.settings.SettingsViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val barcodeDao: BarcodeDao,
    val datastoreManager: DatastoreManager
) : ViewModel() {

    val openUrlInIncognitoMode: StateFlow<Boolean> = datastoreManager.readBooleans(
        SettingsViewModel.SettingItemKey.OPEN_WEB_IN_INCOGNITO.key
    ).map {
        it[SettingsViewModel.SettingItemKey.OPEN_WEB_IN_INCOGNITO.key] ?: false
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

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
            is UiAction.SaveAndViewFirstBarcodeDetail -> {
                saveAndViewFirstBarcode(uiAction.barcodes)
            }
            UiAction.BarcodeOpened -> {
                onBarcodeOpened()
            }
            is UiAction.OpenBarcodeLink -> {
                openBarcode(uiAction.barcode)
            }
            is UiAction.CopyLink -> {
                setLinkToCopy(uiAction.url)
            }
            UiAction.LinkCopied -> {
                setLinkToCopy(null)
            }
            is UiAction.ShareText -> {
                setTextToShare(uiAction.text)
            }
            UiAction.TextShared -> {
                setTextToShare(null)
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

    private fun saveAndViewFirstBarcode(barcodes: List<Barcode>) {
        if (barcodes.isEmpty()) return
        viewModelScope.launch(Dispatchers.IO) {
            val id = barcodeDao.insert(barcodes.first().toEntity()).toInt()
            val event = uiEvent.value
            val barcode = barcodes.first().copy(id = id)
            if (barcode != event.barcodeToViewDetail) {
                _uiEvent.value = event.copy(barcodeToViewDetail = barcode)
            }
            for (i in 1 until barcodes.size) {
                barcodeDao.insert(barcodes[i].toEntity())
            }
        }
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

    private fun setLinkToCopy(url: String?) {
        val state = uiEvent.value
        if (url == state.linkToCopy) return
        _uiEvent.value = state.copy(
            linkToCopy = url
        )
    }

    private fun setTextToShare(text: String?) {
        val state = uiEvent.value
        if (text == state.textToShare) return
        _uiEvent.value = state.copy(
            textToShare = text
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
        data class SaveAndViewFirstBarcodeDetail(val barcodes: List<Barcode>) : UiAction()
        object LinkOpened : UiAction()
        object BarcodeOpened : UiAction()
        object BarcodeDetailPageOpened : UiAction()
        data class CopyLink(val url: String) : UiAction()
        object LinkCopied : UiAction()
        data class ShareText(val text: String) : UiAction()
        object TextShared : UiAction()
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
        val barcodeToViewDetail: Barcode? = null,
        val linkToCopy: String? = null,
        val textToShare: String? = null
    )

    companion object {
        const val NAVIGATION_HOME = "home"
        const val NAVIGATION_HISTORY = "history"
        const val NAVIGATION_SETTINGS = "settings"
        const val NAVIGATION_DETAIL = "detail"
    }
}
