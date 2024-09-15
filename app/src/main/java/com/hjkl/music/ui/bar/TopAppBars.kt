package com.hjkl.music.ui.bar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawerSearchTopAppBar(
    title: String,
    onDrawerClicked: () -> Unit,
    onSearchClicked: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = { Text(text = title) },
        navigationIcon = {
            IconButton(onClick = onDrawerClicked) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = null
                )
            }
        },
        actions = {
            IconButton(onClick = onSearchClicked) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null
                )
            }
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawerSortTopAppBar(
    title: String,
    onDrawerClicked: () -> Unit,
    onSortClicked: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = { Text(text = title) },
        navigationIcon = {
            IconButton(onClick = onDrawerClicked) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = null
                )
            }
        },
        actions = {
            IconButton(onClick = onSortClicked) {
                Icon(
                    imageVector = Icons.Default.Sort,
                    contentDescription = null
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TitleAnimatedVisibleTopBar(title: String, visible: Boolean, onBackClicked: () -> Unit) {
    CenterAlignedTopAppBar(title = {
        AnimatedVisibility(visible = visible) {
            Text(text = title)
        }
    }, navigationIcon = {
        IconButton(onClick = onBackClicked) {
            Icon(imageVector = Icons.Outlined.ArrowBackIosNew, contentDescription = null)
        }
    })
}
