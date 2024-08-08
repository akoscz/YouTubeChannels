package com.akoscz.youtubechannels.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.akoscz.youtubechannels.data.models.room.Video
import com.akoscz.youtubechannels.data.models.room.formattedViewCount
import com.akoscz.youtubechannels.data.models.room.timeAgo

@Composable
fun VideoRow(
    video: Video,
    onPlayClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        VideoThumbnailPlayImage(
            video = video,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .width(160.dp)
                .height(90.dp),
            onPlayClicked = { onPlayClicked() },
            imageType = "medium"
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            verticalArrangement = Arrangement.Top,
        ) {
            Text(
                text = video.title,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 2,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${video.formattedViewCount()} views • ${video.timeAgo()}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
            )
        }
    }
}

@Preview
@Composable
fun VideoRowPreview() {
    VideoRow(
        Video(
            id = "1",
            publishedAt = "2021-08-01T00:00:00Z",
            channelId = "1",
            title = "Title",
            description = "Description",
            defaultThumbnailUrl = "https://via.placeholder.com/150",
            defaultThumbnailWidth = 150,
            defaultThumbnailHeight = 150,
            mediumThumbnailUrl = "https://via.placeholder.com/150",
            mediumThumbnailWidth = 150,
            mediumThumbnailHeight = 150,
            highThumbnailUrl = "https://via.placeholder.com/150",
            highThumbnailWidth = 150,
            highThumbnailHeight = 150,
            standardThumbnailUrl = null,
            standardThumbnailWidth = null,
            standardThumbnailHeight = null,
            maxresThumbnailUrl = null,
            maxresThumbnailWidth = null,
            maxresThumbnailHeight = null,
            channelTitle = "Channel Title",
            defaultLanguage = "en",
            defaultedAudioLanguage = "en",
            duration = "PT1H1M1S",
            contentYtRating = "ytRating",
            viewCount = 1000,
            likeCount = 1000,
            favoriteCount = 1000,
            commentCount = 1000,
            embedHtml = "embedHtml"
        ),
        onPlayClicked = {}
    )
}