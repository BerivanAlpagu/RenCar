package com.turkcell.rencar

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.turkcell.rencar.navigation.RenCarNavHost
import com.turkcell.rencar.ui.RenCarAppViewModel
import com.turkcell.rencar.ui.theme.RenCarTheme

@Composable
fun RenCarApp(
    viewModel: RenCarAppViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val systemDark = isSystemInDarkTheme()

    RenCarTheme(darkTheme = systemDark) {
        RenCarNavHost(
            appState = uiState,
            onEvent = viewModel::onEvent
        )
    }
}
