package com.akoscz.youtubechannels.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.akoscz.youtubechannels.data.models.room.ChannelDetails
import com.akoscz.youtubechannels.data.models.room.getFormattedSubscriberCount
import com.akoscz.youtubechannels.data.models.room.getFormattedVideoCount
import com.akoscz.youtubechannels.data.models.room.getFormattedViewCount
import com.akoscz.youtubechannels.data.models.room.getModifiedBannerUrl

@Composable
fun ChannelDetailsHeader(channelDetails: ChannelDetails) {
    // Banner Image
    AsyncImage(
        model = channelDetails.getModifiedBannerUrl(LocalConfiguration.current.screenWidthDp),
        contentDescription = "Channel Banner",
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(start = 16.dp, end = 16.dp)
            .clip(
                RoundedCornerShape(
                    bottomStart = 12.dp, bottomEnd = 12.dp,
                    topStart = 12.dp, topEnd = 12.dp
                )
            ),
        contentScale = ContentScale.Crop
    )

    // Channel Info Row
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Thumbnail
        AsyncImage(
            model = channelDetails.thumbnailMediumUrl,
            contentDescription = "Channel Thumbnail",
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Channel Name, Custom URL, Subscriber Count
        Column {
            Text(
                channelDetails.title,
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                channelDetails.customUrl ?: "",
                style = MaterialTheme.typography.bodySmall
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "${channelDetails.getFormattedSubscriberCount()} subscribers",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    "${channelDetails.getFormattedVideoCount()} videos",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    "${channelDetails.getFormattedViewCount()} views",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }

    // Expanding Description
    var expanded by remember { mutableStateOf(false) }
    val maxLines = if (expanded) Int.MAX_VALUE else 2
    Text(
        text = channelDetails.description,
        style = MaterialTheme.typography.bodySmall,
        maxLines = maxLines, overflow = TextOverflow.Ellipsis,
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp)
            .clickable { expanded = !expanded }
    )
}
