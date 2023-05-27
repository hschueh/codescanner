package com.gonnaggstudio.codescanner.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.gonnaggstudio.codescanner.MainViewModel
import com.gonnaggstudio.codescanner.R
import com.gonnaggstudio.codescanner.ui.utils.SwipeToDismissBarcodeRecord
import com.gonnaggstudio.codescanner.ui.utils.hiltActivityViewModel

@Composable
fun HistoryScreen(
    historyViewModel: HistoryViewModel = hiltViewModel(),
    mainViewModel: MainViewModel = hiltActivityViewModel()
) {
    val state: HistoryViewModel.UiState = historyViewModel.uiState.collectAsState().value
    val barcodes = state.barcodes.collectAsLazyPagingItems()
    LazyColumn {
        if (barcodes.itemCount == 0 && barcodes.loadState.refresh is LoadState.NotLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_history),
                        style = MaterialTheme.typography.body1,
                    )
                }
            }
        }
        items(
            count = barcodes.itemCount
        ) { index ->
            barcodes[index]?.let { item ->
                SwipeToDismissBarcodeRecord(
                    barcode = item,
                    onDelete = {
                        historyViewModel.onAction(HistoryViewModel.UiAction.DeleteBarcode(it))
                    },
                    onCopy = {
                        mainViewModel.onAction(MainViewModel.UiAction.CopyLink(item.url))
                    },
                    onOpen = {
                        mainViewModel.onAction(MainViewModel.UiAction.OpenUrlLink(item.url))
                    },
                    onShare = {
                        mainViewModel.onAction(MainViewModel.UiAction.ViewBarcodeDetail(item))
                    },
                )
                Divider(
                    Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color.Gray)
                )
            }
        }
    }
}
