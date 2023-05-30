package com.gonnaggstudio.codescanner.ext

import com.gonnaggstudio.codescanner.db.entity.BarcodeEntity
import com.gonnaggstudio.codescanner.model.Barcode
import com.google.mlkit.vision.barcode.common.Barcode as MLKitBarcode

fun MLKitBarcode.toEntity(): BarcodeEntity = BarcodeEntity(
    id = 0,
    barcodeValue = requireNotNull(rawBytes),
    url = requireNotNull(url?.url),
    scannedAt = System.currentTimeMillis(),
    lastInteractAt = System.currentTimeMillis()
)

fun MLKitBarcode.toBarcode(): Barcode = Barcode(
    id = 0,
    barcodeValue = requireNotNull(rawBytes),
    url = requireNotNull(url?.url),
    scannedAt = System.currentTimeMillis(),
    lastInteractAt = System.currentTimeMillis()
)

fun MLKitBarcode.toBarcodeSafe(): Barcode? {
    return if (rawBytes == null || url == null) {
        null
    } else {
        Barcode(
            id = 0,
            barcodeValue = requireNotNull(rawBytes),
            url = requireNotNull(url?.url),
            scannedAt = System.currentTimeMillis(),
            lastInteractAt = System.currentTimeMillis()
        )
    }
}

fun Barcode.toEntity(): BarcodeEntity = BarcodeEntity(
    id = id,
    barcodeValue = barcodeValue,
    url = url,
    scannedAt = scannedAt,
    lastInteractAt = lastInteractAt
)

fun BarcodeEntity.toBarcode(): Barcode = Barcode(
    id = id,
    barcodeValue = barcodeValue,
    url = url,
    scannedAt = scannedAt,
    lastInteractAt = lastInteractAt,
)
