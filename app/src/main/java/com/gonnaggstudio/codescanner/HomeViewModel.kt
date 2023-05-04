package com.gonnaggstudio.codescanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.barcode.common.Barcode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    val uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Scanning(emptyList()))

    fun onAction(uiAction: UiAction) {
        when (uiAction) {
            is UiAction.OnBarcodeReceived -> {
                viewModelScope.launch {
                    uiState.value = withContext(Dispatchers.Default) {
                        UiState.Scanning(
                            uiAction.list.sortedBy { it.displayValue }
                        )
                    }
                }
            }
            is UiAction.OnBarcodeClicked -> {
                // TODO
            }
        }
    }
    sealed class UiAction {
        data class OnBarcodeReceived(val list: List<Barcode>) : UiAction()
        data class OnBarcodeClicked(val barcode: Barcode) : UiAction()
    }

    sealed class UiState {
        object PermissionDenied : UiState()
        data class Scanning(val list: List<Barcode>) : UiState()
    }
}
