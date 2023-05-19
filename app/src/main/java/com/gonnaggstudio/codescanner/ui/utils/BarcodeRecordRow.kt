package com.gonnaggstudio.codescanner.ui.utils

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.gonnaggstudio.codescanner.R

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
