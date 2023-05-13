package com.gonnaggstudio.codescanner.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.findViewTreeViewModelStoreOwner

@Composable
inline fun <reified VM : ViewModel> hiltActivityViewModel(
    viewModelStoreOwner: ViewModelStoreOwner = checkNotNull(LocalView.current.findViewTreeViewModelStoreOwner()) {
        "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
    }
): VM = hiltViewModel(viewModelStoreOwner)
