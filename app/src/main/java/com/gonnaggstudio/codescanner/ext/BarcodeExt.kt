package com.gonnaggstudio.codescanner.ext

import com.gonnaggstudio.codescanner.db.entity.BarcodeEntity
import com.google.mlkit.vision.barcode.common.Barcode

fun Barcode.toEntity(): BarcodeEntity = BarcodeEntity(
    id = 0,
    barcodeValue = requireNotNull(rawBytes),
    url = requireNotNull(url?.url),
    scannedAt = System.currentTimeMillis(),
    lastInteractAt = System.currentTimeMillis()
)
