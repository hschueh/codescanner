package com.gonnaggstudio.codescanner.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gonnaggstudio.codescanner.db.dao.BarcodeDao
import com.gonnaggstudio.codescanner.model.Barcode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val detailArgs: DetailArg = DetailArg(savedStateHandle)

    @Inject
    lateinit var barcodeDao: BarcodeDao

    private val _uiState: MutableStateFlow<UiState> by lazy {
        MutableStateFlow(UiState())
    }

    val uiState: StateFlow<UiState> = _uiState

    fun onAction(uiAction: UiAction) {
        when (uiAction) {
            is UiAction.OnBarcodeClicked -> {
                onBarcodeClicked(uiAction.url)
            }
            UiAction.BarcodeOpened -> {
                onBarcodeOpened()
            }
            UiAction.BarcodeDetailPageOpened -> {
                viewModelScope.launch {
                    val barcode = barcodeDao.getBarcodeById(detailArgs.id)?.let(Barcode::fromEntity)
                    _uiState.value = _uiState.value.copy(barcode = barcode)
                }
            }
        }
    }

    private fun onBarcodeClicked(url: String) {
        val state = uiState.value
        if (url == state.barcodeToOpen) return
        _uiState.value = state.copy(
            barcodeToOpen = url
        )
        /* TODO: find and update lastInteractAt
        viewModelScope.launch(Dispatchers.IO) {
        }
         */
    }

    private fun onBarcodeOpened() {
        _uiState.value = uiState.value.copy(
            barcodeToOpen = null
        )
    }

    sealed class UiAction {
        data class OnBarcodeClicked(val url: String) : UiAction()
        object BarcodeOpened : UiAction()
        object BarcodeDetailPageOpened : UiAction()
    }

    data class UiState(
        val barcode: Barcode? = null,
        val barcodeToOpen: String? = null,
    )
}

class DetailArg(val id: Int) {
    constructor(savedStateHandle: SavedStateHandle) : this(id = checkNotNull(savedStateHandle["barcodeId"]))
}
