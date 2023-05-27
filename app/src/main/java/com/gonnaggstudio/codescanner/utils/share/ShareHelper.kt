package com.gonnaggstudio.codescanner.utils.share

import android.content.Context
import android.content.Intent
import com.gonnaggstudio.codescanner.R
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class ShareHelper @Inject constructor(@ActivityContext val context: Context) {

    fun shareText(textToShare: String) {
        try {
            /*Create an ACTION_SEND Intent*/
            val intent = Intent(Intent.ACTION_SEND)
            /*The type of the content is text, obviously.*/
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, textToShare)
            context.startActivity(
                Intent.createChooser(
                    intent,
                    context.getString(R.string.share_using)
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
