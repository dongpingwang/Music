package com.hjkl.music.ui.custom

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun ScrollableText() {
    val w = 200.dp
    Box(modifier = Modifier.fillMaxWidth()) {
        Text(text = "aaaaa", modifier = Modifier.fillMaxWidth().offset(-w))
        Text(text = "bbbbb", modifier = Modifier.fillMaxWidth())
        Text(text = "ccccc", modifier = Modifier.fillMaxWidth().offset(w))
    }
}