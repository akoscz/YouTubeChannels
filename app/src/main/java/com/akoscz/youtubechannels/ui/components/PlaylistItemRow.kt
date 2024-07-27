package com.akoscz.youtubechannels.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.akoscz.youtubechannels.data.models.room.Playlist


@Composable
fun PlaylistItemRow(playlist: Playlist, channelTitle: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        // Thumbnail with Item Count Overlay
        Box {
            AsyncImage(
                model = playlist.mediumThumbnailUrl,
                contentDescription = "Playlist Thumbnail",
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .width(160.dp)
                    .height(90.dp),
                alignment = Alignment.Center,
                contentScale = ContentScale.FillBounds,
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .padding(end = 10.dp, bottom = 10.dp),
                contentAlignment = Alignment.BottomEnd,
            ) {
                Row (
                    modifier = Modifier
                        .background(
                            color = Color.Black.copy(alpha = 0.7f),
                            shape = RoundedCornerShape(4.dp)
                        ).padding(3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.List,
                        contentDescription = "Playlist Item Count",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = playlist.itemCount.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White,
                    )
                }
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            verticalArrangement = Arrangement.Top,
        ) {
            Text(
                text = playlist.title,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 2,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$channelTitle - Playlist",
                style = MaterialTheme.typography.bodySmall,
                color = Color.DarkGray,
            )
        }
    }
}