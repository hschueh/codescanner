package com.gonnaggstudio.codescanner

import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.gonnaggstudio.codescanner.ui.MainScreen
import com.gonnaggstudio.codescanner.utils.clipboard.ClipboardManagerHelper
import com.gonnaggstudio.codescanner.utils.share.ShareHelper
import com.gonnaggstudio.codescanner.utils.web.CustomTabUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    @Inject
    lateinit var customTabUtils: CustomTabUtils

    @Inject
    lateinit var clipboardManagerHelper: ClipboardManagerHelper

    @Inject
    lateinit var shareHelper: ShareHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(ComposeView(this))

        setContent {
            MaterialTheme(
                colors = MaterialTheme.colors.copy(
                    primary = colorResource(id = R.color.purple_500),
                    primaryVariant = colorResource(id = R.color.purple_700),
                    secondary = colorResource(id = R.color.teal_200),
                    secondaryVariant = colorResource(id = R.color.teal_700),
                ),
                typography = MaterialTheme.typography,
                shapes = MaterialTheme.shapes
            ) {
                MainScreen()
            }
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.uiEvent.collect { event ->
                    when {
                        event.barcodeToOpen != null -> {
                            customTabUtils.launchUri(
                                context = this@MainActivity,
                                uri = Uri.parse(event.barcodeToOpen.url),
                                openNewIncognitoTab = mainViewModel.openUrlInIncognitoMode.value
                            )
                            mainViewModel.onAction(MainViewModel.UiAction.BarcodeOpened)
                        }
                        event.linkToOpen != null -> {
                            customTabUtils.launchUri(
                                context = this@MainActivity,
                                uri = Uri.parse(event.linkToOpen),
                                openNewIncognitoTab = mainViewModel.openUrlInIncognitoMode.value
                            )
                            mainViewModel.onAction(MainViewModel.UiAction.LinkOpened)
                        }
                        event.barcodeToViewDetail != null -> {
                            mainViewModel.onAction(MainViewModel.UiAction.GoToDetailPage(event.barcodeToViewDetail.id))
                            mainViewModel.onAction(MainViewModel.UiAction.BarcodeDetailPageOpened)
                        }
                        event.linkToCopy != null -> {
                            clipboardManagerHelper.copyText(event.linkToCopy)
                            mainViewModel.onAction(MainViewModel.UiAction.LinkCopied)
                        }
                        event.textToShare != null -> {
                            shareHelper.shareText(event.textToShare)
                            mainViewModel.onAction(MainViewModel.UiAction.TextShared)
                        }
                    }
                }
            }
        }
    }
}
