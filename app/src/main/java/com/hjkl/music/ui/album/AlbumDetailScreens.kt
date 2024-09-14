package com.hjkl.music.ui.album

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hjkl.entity.Album
import com.hjkl.music.test.FakeDatas
import com.hjkl.music.ui.comm.AlbumImage
import com.hjkl.music.ui.comm.CommonSongItem
import com.hjkl.music.utils.DisplayUtil
import com.hjkl.player.util.parseMillisTimeToMmSs


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumDetailScreens(album: Album, onBackClicked: () -> Unit) {
    val listState = rememberLazyListState()
    val showTopBarTitle by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0
        }
    }

    val year = if (album.getYear() != null) {
        album.getYear().toString()
    } else {
        "-"
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        CenterAlignedTopAppBar(title = {
            AnimatedVisibility(visible = showTopBarTitle) {
                Text(text = album.name)
            }
        }, navigationIcon = {
            IconButton(onClick = onBackClicked) {
                Icon(imageVector = Icons.Outlined.ArrowBackIosNew, contentDescription = null)
            }
        })
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .systemBarsPadding()
                .padding(top = 64.dp)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ) {
            item {
                Column {
                    Row {
                        AlbumImage(
                            data = album.getAlbumArtBitmap(),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .size(128.dp)
                                .clip(MaterialTheme.shapes.medium)
                        )
                        Column(
                            modifier = Modifier
                                .padding(start = 16.dp)
                                .align(Alignment.CenterVertically)
                        ) {
                            Text(
                                text = album.name,
                                maxLines = 2,
                                minLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = DisplayUtil.getDisplayArtist(album.getArtist()),
                                maxLines = 2,
                                minLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 36.dp, max = 40.dp),
                    ) {
                        Column(
                            modifier = Modifier.weight(1F),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "${album.getSongCount()}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(text = "歌曲", style = MaterialTheme.typography.labelSmall)
                        }
                        Column(
                            modifier = Modifier.weight(1F),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = album.getTotalDuration().parseMillisTimeToMmSs(),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(text = "时长", style = MaterialTheme.typography.labelSmall)
                        }
                        Column(
                            modifier = Modifier.weight(1F),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = year,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(text = "年份", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
            }
            itemsIndexed(album.getSongs()) { index, song ->
                CommonSongItem(order = index + 1, song = song)
            }
        }
    }
}

@Preview
@Composable
private fun AlbumDetailScreensPreview() {
    AlbumDetailScreens(FakeDatas.album, onBackClicked = {})
}