package com.gonnaggstudio.codescanner.ui.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gonnaggstudio.codescanner.ui.utils.BarcodeRecordRow

@Composable
fun DetailScreen(
    detailViewModel: DetailViewModel = hiltViewModel(),
) {
    val state: DetailViewModel.UiState by detailViewModel.uiState.collectAsState()

    LaunchedEffect(detailViewModel.detailArgs.id) {
        detailViewModel.onAction(DetailViewModel.UiAction.BarcodeDetailPageLaunched)
    }
    state.barcode?.let { barcode ->
        Column {
            BarcodeRecordRow(
                label = barcode.url,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
    }
}
