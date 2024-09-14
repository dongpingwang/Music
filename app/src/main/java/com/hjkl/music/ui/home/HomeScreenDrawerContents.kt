package com.hjkl.music.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

val drawerWidth = 200.dp

@Composable
fun HomeScreenDrawerContents(
    modifier: Modifier = Modifier,
    selectedScreen: Screen,
    onScreenSelected: (Screen) -> Unit
) {
    Column(
        modifier = modifier
            .width(drawerWidth)
            .systemBarsPadding()
            .padding(horizontal = 16.dp)
            .padding(top = 72.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Screen.entries.forEach {
            NavigationDrawerItem(label = { Text(it.text) },
                icon = {
                    Icon(imageVector = it.icon, contentDescription = it.text)
                },
                selected = selectedScreen == it,
                onClick = {
                    onScreenSelected(it)
                })
        }
    }
}