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
import com.gonnaggstudio.codescanner.web.CustomTabUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    private val homeViewModel: HomeViewModel by viewModels()

    @Inject
    lateinit var customTabUtils: CustomTabUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(ComposeView(this))

        setContent {
            MainScreen()
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.uiState.collect {
                    when (it) {
                        is HomeViewModel.UiState.Scanning -> {
                            val url = it.barcodeToOpen?.url?.url ?: return@collect
                            customTabUtils.launchUri(this@MainActivity, Uri.parse(url))
                            homeViewModel.onAction(HomeViewModel.UiAction.BarcodeOpened)
                        }
                        else -> {}
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
