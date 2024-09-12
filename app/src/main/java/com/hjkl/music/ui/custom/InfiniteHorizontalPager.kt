package com.hjkl.music.ui.custom

import android.annotation.SuppressLint
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerScope
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import com.hjkl.comm.d
import com.hjkl.comm.tag
import kotlinx.coroutines.launch

private var prePage = 0

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun InfiniteHorizontalPager(
    realIndex: Int,
    realDataSize: Int,
    modifier: Modifier = Modifier,
    pageContent: @Composable PagerScope.(page: Int, state: PagerState) -> Unit,
    onScrollToNext: () -> Unit,
    onScrollToPrevious: () -> Unit,
) {
    "InfiniteHorizontalPager: $realIndex".tag("wdp0").d()
    val initialPage = realIndex + realDataSize * 10
    val pagerState = rememberPagerState(initialPage = initialPage) { Int.MAX_VALUE }
    val scope = rememberCoroutineScope()
    scope.launch {
        pagerState.scrollToPage(initialPage)
    }
    prePage = initialPage
    "prepage: $prePage".tag("wdp0").d()

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            if (prePage < page) {
                onScrollToNext()
            } else if (prePage > page) {
                onScrollToPrevious()
            } else {
            }
            prePage = page
        }
    }

    HorizontalPager(state = pagerState, modifier = modifier) {
        pageContent(it % realDataSize, pagerState)
    }
}