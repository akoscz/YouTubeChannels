package com.akoscz.youtubechannels.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.akoscz.youtubechannels.data.models.room.Video
import com.akoscz.youtubechannels.data.models.room.formattedDuration


@Composable
fun VideoThumbnailPlayImage(
    video: Video,
    onPlayClicked: () -> Unit,
    modifier: Modifier,
    imageType: String
) {
    val imageUrl = when (imageType) {
        "default" -> video.defaultThumbnailUrl
        "medium" -> video.mediumThumbnailUrl
        "high" -> video.highThumbnailUrl
        "standard" -> video.standardThumbnailUrl ?: video.defaultThumbnailUrl
        "maxres" -> video.maxresThumbnailUrl ?: video.defaultThumbnailUrl
        else -> video.defaultThumbnailUrl
    }
    Box(
        modifier = Modifier
            .wrapContentHeight()
            .wrapContentWidth()
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = "Video Thumbnail",
            modifier = modifier,
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center
        )
        Box(
            modifier = modifier
                .padding(end = 10.dp, bottom = 10.dp),
            contentAlignment = Alignment.BottomEnd,
        ) {
            Row(
                modifier = Modifier
                    .background(
                        color = Color.Black.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = video.formattedDuration(),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White,
                )
            }
        }
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Play Button",
                tint = Color.White,
                modifier = Modifier
                    .background(
                        color = Color.Black.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(50)
                    )
                    .padding(16.dp)
                    .clickable { onPlayClicked() }
            )
        }
    }
}

@Preview
@Composable
fun VideoThumbnailPlayImagePreview() {
    VideoThumbnailPlayImage(
        video = Video(
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
        onPlayClicked = {},
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f),
        imageType = "high"
    )
}

@Preview
@Composable
fun VideoThumbnailPlayImagePreview2() {
    VideoThumbnailPlayImage(
        video = Video(
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
        onPlayClicked = {},
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .width(160.dp)
            .height(90.dp),
        imageType = "medium"
    )
}