/*
 * Copyright © 2021 Asia Value Capital. All rights reserved.
 * Created by Hoso on 2021/9/22.
 */

package com.gonnaggstudio.codescanner.utils.web

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.browser.customtabs.CustomTabsIntent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CustomTabUtils @Inject constructor() {
    fun launchUri(context: Context, uri: Uri, openNewIncognitoTab: Boolean = false) {
        // Maybe handled by specific app.
        val launched = if (Build.VERSION.SDK_INT >= 30) launchNativeApi30(
            context,
            uri,
            openNewIncognitoTab
        ) else launchNativeBeforeApi30(
            context,
            uri,
            openNewIncognitoTab
        )
        if (launched) return

        try {
            CustomTabsIntent.Builder()
                .build()
                .apply {
                    intent.decorateForIncognito(openNewIncognitoTab)
                }
                .launchUrl(context, uri)
        } catch (e: Exception) {
            val intent = Intent(context, WebviewActivity::class.java)
            intent.putExtra(WebviewActivity.EXTRA_URL, uri.toString())
            context.startActivity(intent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun launchNativeApi30(context: Context, uri: Uri, openNewIncognitoTab: Boolean = false): Boolean {
        val nativeAppIntent = Intent(Intent.ACTION_VIEW, uri)
            .addCategory(Intent.CATEGORY_BROWSABLE)
            .addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_REQUIRE_NON_BROWSER
            )
            .decorateForIncognito(openNewIncognitoTab)
        return try {
            context.startActivity(nativeAppIntent)
            true
        } catch (ex: ActivityNotFoundException) {
            false
        }
    }

    private fun launchNativeBeforeApi30(context: Context, uri: Uri, openNewIncognitoTab: Boolean = false): Boolean {
        val pm: PackageManager = context.packageManager

        // Get all Apps that resolve a generic url
        val browserActivityIntent = Intent()
            .setAction(Intent.ACTION_VIEW)
            .addCategory(Intent.CATEGORY_BROWSABLE)
            .setData(Uri.fromParts("http", "", null))
            .decorateForIncognito(openNewIncognitoTab)
        val genericResolvedList: Set<String> = extractPackageNames(
            pm.queryIntentActivities(browserActivityIntent, 0)
        )

        // Get all apps that resolve the specific Url
        val specializedActivityIntent = Intent(Intent.ACTION_VIEW, uri)
            .addCategory(Intent.CATEGORY_BROWSABLE)
        val resolvedSpecializedList: MutableSet<String> = extractPackageNames(
            pm.queryIntentActivities(specializedActivityIntent, 0)
        )

        // Keep only the Urls that resolve the specific, but not the generic
        // urls.
        resolvedSpecializedList.removeAll(genericResolvedList)

        // If the list is empty, no native app handlers were found.
        if (resolvedSpecializedList.isEmpty()) {
            return false
        }

        // We found native handlers. Launch the Intent.
        specializedActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(specializedActivityIntent)
        return true
    }

    private fun extractPackageNames(list: List<ResolveInfo>): MutableSet<String> {
        val mutableSet = mutableSetOf<String>()
        list.forEach {
            mutableSet.add(it.activityInfo.packageName)
        }
        return mutableSet
    }

    private fun Intent.decorateForIncognito(openNewIncognitoTab: Boolean) = this.apply {
        if (openNewIncognitoTab) {
            putExtra(INCOGNITO_FLAG, true)
        }
    }

    companion object {
        const val INCOGNITO_FLAG = "com.google.android.apps.chrome.EXTRA_OPEN_NEW_INCOGNITO_TAB"
    }
}
