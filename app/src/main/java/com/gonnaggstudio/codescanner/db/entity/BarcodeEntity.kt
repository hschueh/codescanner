package com.gonnaggstudio.codescanner.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.gonnaggstudio.codescanner.db.Constants.BARCODE_TABLE

@Entity(
    tableName = BARCODE_TABLE,
    indices = [Index(value = ["scanned_at", "url"])]
)
data class BarcodeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "barcode_value", typeAffinity = ColumnInfo.BLOB)
    val barcodeValue: ByteArray,
    @ColumnInfo(name = "url")
    val url: String,
    @ColumnInfo(name = "scanned_at")
    val scannedAt: Long,
    @ColumnInfo(name = "last_interact_at")
    val lastInteractAt: Long,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BarcodeEntity

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
