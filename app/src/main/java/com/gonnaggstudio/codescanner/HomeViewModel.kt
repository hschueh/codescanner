package com.gonnaggstudio.codescanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.barcode.common.Barcode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Scanning())
    val uiState: StateFlow<UiState> = _uiState

    fun onAction(uiAction: UiAction) {
        val state = uiState.value
        // Handle Scanning Only Action.
        if (state is UiState.Scanning) {
            when (uiAction) {
                is UiAction.OnBarcodeReceived -> {
                    onBarcodeReceived(
                        uiAction.list.take(DISPLAYED_URL).sortedBy { it.displayValue }
                    )
                }
                is UiAction.OnBarcodeClicked -> {
                    onBarcodeClicked(
                        uiAction.barcode
                    )
                }
                is UiAction.BarcodeOpened -> {
                    onBarcodeOpened()
                }
            }
        }
    }
    private fun onBarcodeReceived(barcodeList: List<Barcode>) {
        val uiState = uiState.value as? UiState.Scanning ?: return
        if (barcodeList == uiState.list) return
        viewModelScope.launch {
            _uiState.value = withContext(Dispatchers.Default) {
                uiState.copy(
                    list = barcodeList
                )
            }
        }
    }

    private fun onBarcodeClicked(barcode: Barcode) {
        val uiState = uiState.value as? UiState.Scanning ?: return
        if (barcode == uiState.barcodeToOpen) return
        _uiState.value = uiState.copy(
            barcodeToOpen = barcode
        )
    }
    private fun onBarcodeOpened() {
        val uiState = uiState.value as? UiState.Scanning ?: return
        _uiState.value = uiState.copy(
            barcodeToOpen = null,
            list = emptyList()
        )
    }

    sealed class UiAction {
        data class OnBarcodeReceived(val list: List<Barcode>) : UiAction()
        data class OnBarcodeClicked(val barcode: Barcode) : UiAction()
        object BarcodeOpened : UiAction()
    }

    sealed class UiState {
        object PermissionDenied : UiState()
        data class Scanning(
            val barcodeToOpen: Barcode? = null,
            val list: List<Barcode> = emptyList()
        ) : UiState()
    }

    companion object {
        const val DISPLAYED_URL = 1
    }
}
