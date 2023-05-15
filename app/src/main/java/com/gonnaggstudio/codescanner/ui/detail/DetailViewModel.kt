package com.gonnaggstudio.codescanner.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gonnaggstudio.codescanner.db.dao.BarcodeDao
import com.gonnaggstudio.codescanner.ext.toBarcode
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
            UiAction.BarcodeDetailPageLaunched -> {
                viewModelScope.launch {
                    val barcode = barcodeDao.getBarcodeById(detailArgs.id)?.toBarcode()
                    _uiState.value = _uiState.value.copy(barcode = barcode)
                }
            }
        }
    }

    sealed class UiAction {
        object BarcodeDetailPageLaunched : UiAction()
    }

    data class UiState(
        val barcode: Barcode? = null
    )
}

class DetailArg(val id: Int) {
    constructor(savedStateHandle: SavedStateHandle) : this(id = checkNotNull(savedStateHandle["barcodeId"]))
}
