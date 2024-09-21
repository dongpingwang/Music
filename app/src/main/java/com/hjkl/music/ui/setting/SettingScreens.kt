package com.hjkl.music.ui.setting

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hjkl.music.R
import com.hjkl.music.ui.comm.ActionHandler

@Composable
fun SettingScreens(onDrawerClicked: () -> Unit) {
    val actionHandler = ActionHandler
    SettingScreens2(
        onDrawerClicked = onDrawerClicked,
        onScanAudioItemClick = actionHandler.navigationActions.navigateToScanAudioSetting
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreens2(
    onDrawerClicked: () -> Unit,
    onScanAudioItemClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        CenterAlignedTopAppBar(title = {
            Text(text = stringResource(id = R.string.setting_title))
        }, navigationIcon = {
            IconButton(onClick = onDrawerClicked) {
                Icon(imageVector = Icons.Filled.Menu, contentDescription = null)
            }
        })
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            item {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onScanAudioItemClick()
                    }
                    .padding(vertical = 8.dp)
                ) {
                    Image(imageVector = Icons.Filled.AudioFile, contentDescription = null)
                    Text(
                        text = stringResource(id = R.string.settings_scan_audio),
                        modifier = Modifier.padding(horizontal = 8.dp),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.weight(1F))
                    Image(imageVector = Icons.Filled.KeyboardArrowRight, contentDescription = null)
                }
            }
            item {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .clickable {

                    }
                    .padding(vertical = 8.dp)
                ) {
                    Image(imageVector = Icons.Filled.Info, contentDescription = null)
                    Text(
                        text = stringResource(id = R.string.settings_about),
                        modifier = Modifier.padding(horizontal = 8.dp),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.weight(1F))
                    Image(imageVector = Icons.Filled.KeyboardArrowRight, contentDescription = null)
                }
            }
        }
    }
}

@Preview
@Composable
private fun SettingScreensPreview() {
    SettingScreens2(onDrawerClicked = {}, onScanAudioItemClick = {})
}