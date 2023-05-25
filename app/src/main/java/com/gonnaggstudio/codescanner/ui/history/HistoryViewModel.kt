package com.gonnaggstudio.codescanner.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.gonnaggstudio.codescanner.db.dao.BarcodeDao
import com.gonnaggstudio.codescanner.ext.toBarcode
import com.gonnaggstudio.codescanner.ext.toEntity
import com.gonnaggstudio.codescanner.model.Barcode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    val barcodeDao: BarcodeDao
) : ViewModel() {

    private val pager = Pager(
        config = PagingConfig(pageSize = 50)
    ) {
        barcodeDao.getAllBarcodesDescPaging()
    }

    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(
        UiState(
            pager.flow.map { pagingData ->
                pagingData.map { it.toBarcode() }
            }
        )
    )

    val uiState: StateFlow<UiState> = _uiState

    fun onAction(uiAction: UiAction) {
        when (uiAction) {
            is UiAction.DeleteBarcode -> {
                viewModelScope.launch(Dispatchers.IO) {
                    uiAction.barcode.toEntity().let {
                        barcodeDao.delete(it)
                    }
                }
            }
        }
    }

    sealed class UiAction {
        data class DeleteBarcode(val barcode: Barcode) : UiAction()
    }

    data class UiState(
        val barcodes: Flow<PagingData<Barcode>>
    )
}
