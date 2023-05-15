package com.gonnaggstudio.codescanner.ui.home

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
                        uiAction.list.take(DISPLAYED_URL_COUNT).sortedBy { it.displayValue }
                    )
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

    sealed class UiAction {
        data class OnBarcodeReceived(val list: List<Barcode>) : UiAction()
    }

    sealed class UiState {
        object PermissionDenied : UiState()
        data class Scanning(
            val list: List<Barcode> = emptyList()
        ) : UiState()
    }

    companion object {
        const val DISPLAYED_URL_COUNT = 1
    }
}
