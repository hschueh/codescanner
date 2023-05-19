package com.gonnaggstudio.codescanner.model

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Parcelable
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import kotlinx.parcelize.Parcelize
import java.nio.charset.StandardCharsets
import java.util.*

@Parcelize
data class Barcode(
    val id: Int,
    val barcodeValue: ByteArray,
    val url: String,
    val scannedAt: Long,
    val lastInteractAt: Long,
) : Parcelable {

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

    // https://github.com/zxing/zxing/blob/master/android/src/com/google/zxing/client/android/encode/QRCodeEncoder.java
    fun encodeAsBitmap(): Bitmap? = try {
        val contentsToEncode = String(barcodeValue, StandardCharsets.ISO_8859_1)
        val hints: Map<EncodeHintType?, Any?>? = guessAppropriateEncoding(contentsToEncode)?.let {
            EnumMap<EncodeHintType, Any?>(EncodeHintType::class.java).apply {
                put(EncodeHintType.CHARACTER_SET, it)
            }
        }
        val result = MultiFormatWriter().encode(contentsToEncode, BarcodeFormat.QR_CODE, 512, 512, hints)
        val width: Int = result.width
        val height: Int = result.height
        val pixels = IntArray(width * height)
        for (y in 0 until height) {
            val offset = y * width
            for (x in 0 until width) {
                pixels[offset + x] = if (result.get(x, y)) Color.BLACK else Color.WHITE
            }
        }
        Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
            setPixels(pixels, 0, width, 0, 0, width, height)
        }
    } catch (e: Exception) {
        null
    }

    private fun guessAppropriateEncoding(contents: CharSequence): String? {
        // Very crude at the moment
        for (element in contents) {
            if (element.code > 0xFF) {
                return "UTF-8"
            }
        }
        return null
    }
}
