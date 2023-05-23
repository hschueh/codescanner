package com.gonnaggstudio.codescanner.ui.ads

import android.view.ViewGroup
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.viewinterop.AndroidView
import com.gonnaggstudio.codescanner.BuildConfig
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun BannerAds(modifier: Modifier = Modifier) {
    var adView: AdView? by remember { mutableStateOf(null) }
    val configuration = LocalConfiguration.current
    LaunchedEffect(adView) {
        val adRequest = AdRequest.Builder().build()
        adView?.loadAd(adRequest)
    }
    AndroidView(
        modifier = modifier,
        factory = { context ->
            AdView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                setAdSize(AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, configuration.screenWidthDp))
                adUnitId = BuildConfig.BANNER_AD_UNIT_ID
            }.also {
                adView = it
            }
        },
        update = {}
    )
}
