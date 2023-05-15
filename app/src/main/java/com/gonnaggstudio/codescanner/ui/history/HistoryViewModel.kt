package com.gonnaggstudio.codescanner.ui.history

import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.gonnaggstudio.codescanner.db.dao.BarcodeDao
import com.gonnaggstudio.codescanner.db.entity.BarcodeEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var barcodeDao: BarcodeDao

    private val pager = Pager(
        config = PagingConfig(pageSize = 50)
    ) {
        barcodeDao.getAllBarcodesDescPaging()
    }

    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState(pager.flow))

    val uiState: StateFlow<UiState> = _uiState

    fun onAction(uiAction: UiAction) {
        when (uiAction) {
            is UiAction.OnBarcodeClicked -> {
                onBarcodeClicked(uiAction.url)
            }
            is UiAction.ViewBarcodeDetail -> {
                navigateToBarcode(uiAction.barcode)
            }
            UiAction.BarcodeDetailPageOpened -> {
                navigateToBarcodeFinished()
            }
            UiAction.BarcodeOpened -> {
                onBarcodeOpened()
            }
        }
    }

    private fun navigateToBarcode(barcode: BarcodeEntity) {
        // TODO: Handle the value in MainActivity and pass the value to MainViewModel, I guess.
    }

    private fun navigateToBarcodeFinished() {
        val state = uiState.value
        if (null == state.barcodeToOpen) return
        _uiState.value = state.copy(
            barcodeToViewDetail = null
        )
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
        data class ViewBarcodeDetail(val barcode: BarcodeEntity) : UiAction()
        object BarcodeOpened : UiAction()
        object BarcodeDetailPageOpened : UiAction()
    }

    data class UiState(
        val barcodes: Flow<PagingData<BarcodeEntity>>,
        val barcodeToOpen: String? = null,
        val barcodeToViewDetail: BarcodeEntity? = null
    )
}
