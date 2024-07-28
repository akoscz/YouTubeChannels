package com.akoscz.youtubechannels.ui.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.paging.compose.LazyPagingItems
import com.akoscz.youtubechannels.data.models.room.Playlist


@Composable
fun PlaylistItemsList(playlistItems: LazyPagingItems<Playlist>, channelTitle: String) {
    LazyColumn {
        items(playlistItems.itemCount) { i ->
            val playlist = playlistItems[i]
            if (playlist != null) {
                PlaylistItemRow(playlist, channelTitle)
            }
        }
    }
}