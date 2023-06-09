package com.gonnaggstudio.codescanner.ui.detail

import android.graphics.Bitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gonnaggstudio.codescanner.db.dao.BarcodeDao
import com.gonnaggstudio.codescanner.ext.toBarcode
import com.gonnaggstudio.codescanner.model.Barcode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    val barcodeDao: BarcodeDao
) : ViewModel() {

    val detailArgs: DetailArg = DetailArg(savedStateHandle)

    private val _uiState: MutableStateFlow<UiState> by lazy {
        MutableStateFlow(UiState())
    }

    val uiState: StateFlow<UiState> = _uiState

    fun onAction(uiAction: UiAction) {
        when (uiAction) {
            UiAction.BarcodeDetailPageLaunched -> {
                viewModelScope.launch(Dispatchers.IO) {
                    val barcode = barcodeDao.getBarcodeById(detailArgs.id)?.toBarcode()
                    _uiState.update {
                        it.copy(
                            barcode = barcode,
                            bitmap = barcode?.encodeAsBitmap(),
                        )
                    }
                }
            }
        }
    }

    sealed class UiAction {
        object BarcodeDetailPageLaunched : UiAction()
    }

    data class UiState(
        val barcode: Barcode? = null,
        val bitmap: Bitmap? = null,
    )
}

class DetailArg(val id: Int) {
    constructor(savedStateHandle: SavedStateHandle) : this(id = checkNotNull(savedStateHandle["barcodeId"]))
}
