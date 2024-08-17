package com.hjkl.music.ui.player

import SongUiState
import SongViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.hjkl.music.ui.theme.MusicTheme



@Composable
fun PlayerPage(uiState: SongUiState) {
    val pagerState = rememberPagerState(initialPage = 1, pageCount = {
        3
    })
    HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
        Text(
            text = "Page: $page",
            modifier = Modifier.fillMaxWidth()
        )
    }

}