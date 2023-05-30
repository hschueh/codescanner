package com.gonnaggstudio.codescanner.ui.detail

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gonnaggstudio.codescanner.MainViewModel
import com.gonnaggstudio.codescanner.R
import com.gonnaggstudio.codescanner.ui.utils.hiltActivityViewModel

@Composable
fun DetailScreen(
    detailViewModel: DetailViewModel = hiltViewModel(),
    mainViewModel: MainViewModel = hiltActivityViewModel()
) {
    val state: DetailViewModel.UiState by detailViewModel.uiState.collectAsState()

    LaunchedEffect(detailViewModel.detailArgs.id) {
        detailViewModel.onAction(DetailViewModel.UiAction.BarcodeDetailPageLaunched)
    }
    /* TOO MANY LAUNCHED EFFECTS
    LaunchedEffect(state.barcode?.url) {
        state.barcode?.url?.let {
            mainViewModel.onAction(MainViewModel.UiAction.ShareText(it))
        }
    }
    */

    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
    ) {
        state.barcode?.let { barcode ->
            Text(text = barcode.url)
            state.bitmap?.let {
                BitmapImage(
                    bitmap = it,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentDescription = null,
                )
            } ?: Text(text = stringResource(R.string.no_bitmap))
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                Image(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(24.dp)
                        .clickable {
                            mainViewModel.onAction(MainViewModel.UiAction.CopyLink(barcode.url))
                        },
                    colorFilter = ColorFilter.tint(Color.Black),
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_content_copy),
                    contentDescription = "Copy Url"
                )
                Image(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(24.dp)
                        .clickable {
                            mainViewModel.onAction(MainViewModel.UiAction.ShareText(barcode.url))
                        },
                    colorFilter = ColorFilter.tint(Color.Black),
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share Url"
                )
                Image(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(24.dp)
                        .clickable {
                            mainViewModel.onAction(MainViewModel.UiAction.OpenUrlLink(barcode.url))
                        },
                    colorFilter = ColorFilter.tint(Color.Black),
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_open_in_new),
                    contentDescription = "Open Url"
                )
            }
        }
    }
}

@Composable
fun BitmapImage(
    bitmap: Bitmap,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) {
    val imageBitmap = remember(bitmap) {
        bitmap.asImageBitmap()
    }
    Image(
        modifier = modifier,
        bitmap = imageBitmap,
        contentDescription = contentDescription,
    )
}
