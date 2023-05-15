package com.gonnaggstudio.codescanner.model

import android.os.Parcelable
import com.gonnaggstudio.codescanner.db.entity.BarcodeEntity
import kotlinx.parcelize.Parcelize

@Parcelize
data class Barcode(
    val id: Int,
    val barcodeValue: ByteArray,
    val url: String,
    val scannedAt: Long,
    val lastInteractAt: Long,
) : Parcelable {
    companion object {
        fun fromEntity(entity: BarcodeEntity): Barcode {
            return Barcode(
                id = entity.id,
                barcodeValue = entity.barcodeValue,
                url = entity.url,
                scannedAt = entity.scannedAt,
                lastInteractAt = entity.lastInteractAt,
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Barcode

        if (id != other.id) return false
        if (!barcodeValue.contentEquals(other.barcodeValue)) return false
        if (url != other.url) return false
        if (scannedAt != other.scannedAt) return false
        if (lastInteractAt != other.lastInteractAt) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + barcodeValue.contentHashCode()
        result = 31 * result + url.hashCode()
        result = 31 * result + scannedAt.hashCode()
        result = 31 * result + lastInteractAt.hashCode()
        return result
    }
}
