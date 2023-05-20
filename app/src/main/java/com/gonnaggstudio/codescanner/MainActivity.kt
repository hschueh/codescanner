package com.gonnaggstudio.codescanner

import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.gonnaggstudio.codescanner.ui.MainScreen
import com.gonnaggstudio.codescanner.utils.clipboard.ClipboardManagerHelper
import com.gonnaggstudio.codescanner.utils.web.CustomTabUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    private val mainViewModel: MainViewModel by viewModels()

    @Inject
    lateinit var customTabUtils: CustomTabUtils

    @Inject
    lateinit var clipboardManagerHelper: ClipboardManagerHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(ComposeView(this))

        setContent {
            MainScreen()
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
                    }
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
