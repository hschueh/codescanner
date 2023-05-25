package com.gonnaggstudio.codescanner.ui.utils

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.gonnaggstudio.codescanner.R
import com.gonnaggstudio.codescanner.model.Barcode

@Composable
fun BarcodeRecordRow(
    onCopy: () -> Unit = {},
    onOpen: () -> Unit = {},
    onShare: () -> Unit = {},
    modifier: Modifier = Modifier,
    label: String,
) {
    Column(
        modifier = modifier
            .background(color = Color.White, shape = MaterialTheme.shapes.medium)
            .padding(8.dp)
    ) {
        Text(text = label, style = MaterialTheme.typography.h5)
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Image(
                modifier = Modifier
                    .padding(4.dp)
                    .size(24.dp)
                    .clickable {
                        onCopy.invoke()
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
                        onShare.invoke()
                    },
                colorFilter = ColorFilter.tint(Color.Black),
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_fullscreen),
                contentDescription = "View Detail"
            )
            Image(
                modifier = Modifier
                    .padding(4.dp)
                    .size(24.dp)
                    .clickable {
                        onOpen.invoke()
                    },
                colorFilter = ColorFilter.tint(Color.Black),
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_open_in_new),
                contentDescription = "Open Url"
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeToDismissBarcodeRecord(
    barcode: Barcode,
    modifier: Modifier = Modifier,
    onDelete: (Barcode) -> Unit = {},
    onCopy: () -> Unit = {},
    onOpen: () -> Unit = {},
    onShare: () -> Unit = {},
) {
    val currentItem by rememberUpdatedState(barcode)
    val dismissState = rememberDismissState(
        confirmStateChange = {
            if (it == DismissValue.DismissedToStart) {
                onDelete.invoke(currentItem)
            }
            true
        }
    )
    LaunchedEffect(barcode) {
        dismissState.reset()
    }
    SwipeToDismiss(
        state = dismissState,
        modifier = modifier,
        directions = setOf(
            DismissDirection.EndToStart
        ),
        dismissThresholds = {
            FractionalThreshold(0.5f)
        },
        background = {
            val color by animateColorAsState(
                when (dismissState.targetValue) {
                    DismissValue.Default -> Color.White
                    else -> Color.Red
                }
            )

            val iconColor by animateColorAsState(
                when (dismissState.targetValue) {
                    DismissValue.Default -> Color.White
                    else -> Color.Black
                }
            )

            val scale by animateFloatAsState(
                if (dismissState.targetValue == DismissValue.Default) 0.75f else 1f
            )

            Box(
                Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete Icon",
                    modifier = Modifier.scale(scale),
                    tint = iconColor
                )
            }
        },
        dismissContent = {
            BarcodeRecordRow(
                onCopy = onCopy,
                onOpen = onOpen,
                onShare = onShare,
                modifier = Modifier.fillMaxWidth(),
                label = barcode.url
            )
        }
    )
}
