package com.gonnaggstudio.codescanner.ui.home

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.barcode.common.Barcode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    fun onAction(uiAction: UiAction) {
        when (uiAction) {
            is UiAction.OnBarcodeReceived -> {
                onBarcodeReceived(
                    uiAction.list.take(DISPLAYED_URL_COUNT).sortedBy { it.displayValue }
                )
            }
            UiAction.OnTorchClicked -> toggleTorch()
            is UiAction.OnImageSelected -> {
                onImageSelected(uiAction.uri)
            }
            UiAction.OnUriDetectionFinish -> {
                onImageSelected(null)
            }
        }
    }
    private fun onBarcodeReceived(barcodeList: List<Barcode>) {
        if (barcodeList == uiState.value.list) return
        viewModelScope.launch(Dispatchers.Default) {
            _uiState.update {
                it.copy(
                    list = barcodeList
                )
            }
        }
    }

    private fun toggleTorch() {
        viewModelScope.launch(Dispatchers.Default) {
            _uiState.update {
                it.copy(
                    isTorchEnabled = !it.isTorchEnabled
                )
            }
        }
    }

    private fun onImageSelected(uri: Uri?) {
        viewModelScope.launch(Dispatchers.Default) {
            _uiState.update {
                it.copy(
                    imageUri = uri
                )
            }
        }
    }

    sealed class UiAction {
        data class OnBarcodeReceived(val list: List<Barcode>) : UiAction()
        object OnTorchClicked : UiAction()
        data class OnImageSelected(val uri: Uri) : UiAction()
        object OnUriDetectionFinish : UiAction()
    }

    data class UiState(
        val list: List<Barcode> = emptyList(),
        val isTorchEnabled: Boolean = false,
        val imageUri: Uri? = null
    )

    companion object {
        const val DISPLAYED_URL_COUNT = 1
    }
}
