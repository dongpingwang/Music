package com.hjkl.music.ui.bar

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.hjkl.music.data.PlayerUiState
import kotlinx.coroutines.launch


@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun BoxScope.PlayerSnackbar(uiState: PlayerUiState) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.BottomEnd))
    if (uiState.playerErrorMsgOnce != null) {
        scope.launch {
            snackbarHostState.showSnackbar(
                uiState.playerErrorMsgOnce,
                withDismissAction = true
            )
        }
    }
    if (uiState.toast != null) {
        scope.launch { snackbarHostState.showSnackbar(uiState.toast, withDismissAction = true) }
    }
}