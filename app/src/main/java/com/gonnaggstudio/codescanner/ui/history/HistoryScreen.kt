package com.gonnaggstudio.codescanner.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.gonnaggstudio.codescanner.MainViewModel
import com.gonnaggstudio.codescanner.ui.utils.BarcodeRecordRow
import com.gonnaggstudio.codescanner.ui.utils.hiltActivityViewModel

@Composable
fun HistoryScreen(
    historyViewModel: HistoryViewModel = hiltViewModel(),
    mainViewModel: MainViewModel = hiltActivityViewModel()
) {
    val state: HistoryViewModel.UiState = historyViewModel.uiState.collectAsState().value
    val barcodes = state.barcodes.collectAsLazyPagingItems()
    LazyColumn {
        items(
            count = barcodes.itemCount
        ) { index ->
            barcodes[index]?.let { item ->
                BarcodeRecordRow(
                    onCopy = {
                        mainViewModel.onAction(MainViewModel.UiAction.CopyLink(item.url))
                    },
                    onOpen = {
                        mainViewModel.onAction(MainViewModel.UiAction.OpenUrlLink(item.url))
                    },
                    onShare = {
                        mainViewModel.onAction(MainViewModel.UiAction.ViewBarcodeDetail(item))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = item.url
                )
                Divider(Modifier.fillMaxWidth().height(1.dp).background(Color.Gray))
            }
        }
    }
}
