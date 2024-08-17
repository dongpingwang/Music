package com.hjkl.music.ui

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hjkl.music.R
import com.hjkl.music.ui.theme.MusicTheme

@Composable
fun AppDrawer(
    currentRoute: String,
    navigateToSong: () -> Unit,
    navigateToAlbum: () -> Unit,
    navigateToArtist: () -> Unit,
    navigateToFolder: () -> Unit,
    navigateToFavorite: () -> Unit,
    navigateToSonglist: () -> Unit,
    navigateToSetting: () -> Unit,
    navigateToAbout: () -> Unit,
    closeDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(modifier) {
        MusicLogo(
            modifier = Modifier.padding(horizontal = 28.dp, vertical = 24.dp)
        )
        NavigationDrawerItem(
            label = { Text(stringResource(id = R.string.song_title)) },
            icon = { Icon(imageVector = ImageVector.vectorResource(id = R.drawable.audio_file_24px), contentDescription = null) },
            selected = currentRoute == MusicDestinations.SONG_ROUTE,
            onClick = { navigateToSong(); closeDrawer() },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
        NavigationDrawerItem(
            label = { Text(stringResource(id = R.string.album_title)) },
            icon = { Icon(imageVector = ImageVector.vectorResource(id = R.drawable.album_24px), null) },
            selected = currentRoute == MusicDestinations.ALBUM_ROUTE,
            onClick = { navigateToAlbum(); closeDrawer() },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
        NavigationDrawerItem(
            label = { Text(stringResource(id = R.string.artist_title)) },
            icon = { Icon( imageVector = ImageVector.vectorResource(id = R.drawable.artist_24px), null) },
            selected = currentRoute == MusicDestinations.ARTIST_ROUTE,
            onClick = { navigateToArtist(); closeDrawer() },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )

        NavigationDrawerItem(
            label = { Text(stringResource(id = R.string.folder_title)) },
            icon = { Icon(imageVector = ImageVector.vectorResource(id = R.drawable.folder_24px), null) },
            selected = currentRoute == MusicDestinations.FOLDER_ROUTE,
            onClick = { navigateToFolder(); closeDrawer() },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )

        Divider()
        Spacer(Modifier.height(8.dp))
        NavigationDrawerItem(
            label = { Text(stringResource(id = R.string.favorite_title)) },
            icon = { Icon(imageVector = ImageVector.vectorResource(id = R.drawable.favorite_24px), null) },
            selected = currentRoute == MusicDestinations.FAVORITE_ROUTE,
            onClick = { navigateToFavorite(); closeDrawer() },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )

        NavigationDrawerItem(
            label = { Text(stringResource(id = R.string.songlist_title)) },
            icon = { Icon(imageVector = ImageVector.vectorResource(id = R.drawable.list_alt_24px), null) },
            selected = currentRoute == MusicDestinations.SONGLIST_ROUTE,
            onClick = { navigateToSonglist(); closeDrawer() },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )

        Divider()
        Spacer(Modifier.height(8.dp))
        NavigationDrawerItem(
            label = { Text(stringResource(id = R.string.setting_title)) },
            icon = { Icon(imageVector = ImageVector.vectorResource(id  = R.drawable.settings_24px), null) },
            selected = currentRoute == MusicDestinations.SETTING_ROUTE,
            onClick = { navigateToSetting(); closeDrawer() },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )

        NavigationDrawerItem(
            label = { Text(stringResource(id = R.string.about_title)) },
            icon = { Icon(imageVector = ImageVector.vectorResource(id = R.drawable.info_24px), null) },
            selected = currentRoute == MusicDestinations.ABOUT_ROUTE,
            onClick = { navigateToAbout(); closeDrawer() },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
    }
}

@Composable
private fun MusicLogo(modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        Icon(
            painterResource(R.drawable.ic_launcher_foreground),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = stringResource(id = R.string.app_name),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

    }
}

@Preview("Drawer contents")
@Preview("Drawer contents (dark)", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewAppDrawer() {
    MusicTheme {
        AppDrawer(
            currentRoute = MusicDestinations.SONG_ROUTE,
            navigateToSong = { },
            navigateToAlbum = { },
            navigateToArtist = {},
            navigateToFolder = {},
            navigateToFavorite = {},
            navigateToSonglist = {},
            navigateToSetting = {},
            navigateToAbout = {},
            closeDrawer = {  }
        )
    }
}
