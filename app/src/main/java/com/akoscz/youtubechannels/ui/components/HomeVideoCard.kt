package com.akoscz.youtubechannels.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
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
import com.akoscz.youtubechannels.data.models.room.ChannelDetails
import com.akoscz.youtubechannels.data.models.room.Video
import com.akoscz.youtubechannels.data.models.room.formattedDuration
import com.akoscz.youtubechannels.data.models.room.formattedViewCount
import com.akoscz.youtubechannels.data.models.room.timeAgo


@Composable
fun HomeVideoCard(video: Video, channelDetails: ChannelDetails) {
    // Display a card with the video thumbnail, title, and channel name

    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ){
        Box {
            AsyncImage(
                model = video.highThumbnailUrl,
                contentDescription = "Video Thumbnail",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f),
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center
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
                        )
                        .padding(3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(
                        text = video.formattedDuration(),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White,
                    )
                }
            }
        }
        Row (
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth()
        ){
            AsyncImage(
                model = channelDetails.thumbnailDefaultUrl,
                contentDescription = "Channel Thumbnail",
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .width(40.dp)
                    .height(40.dp),
                alignment = Alignment.Center,
                contentScale = ContentScale.FillBounds,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = video.title,
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${channelDetails.title} • ${video.formattedViewCount()} views • ${video.timeAgo()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}