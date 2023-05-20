package com.gonnaggstudio.codescanner.utils.clipboard

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClipboardManagerHelper @Inject constructor(val context: Context) {

    private val clipboard: ClipboardManager by lazy {
        getSystemService(
            context,
            ClipboardManager::class.java
        ) as ClipboardManager
    }

    fun copyText(
        text: String,
        label: String = "copied_url"
    ) {
        val clip: ClipData = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(
            context,
            "$text copied to clipboard",
            Toast.LENGTH_SHORT
        ).show()
    }
}
