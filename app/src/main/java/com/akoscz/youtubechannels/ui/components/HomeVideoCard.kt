package com.akoscz.youtubechannels.ui.components

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.akoscz.youtubechannels.data.models.room.ChannelDetails
import com.akoscz.youtubechannels.data.models.room.Video
import com.akoscz.youtubechannels.data.models.room.formattedViewCount
import com.akoscz.youtubechannels.data.models.room.timeAgo
import com.akoscz.youtubechannels.ui.VideoPlayerActivity

@Composable
fun HomeVideoCard(
    video: Video,
    channelDetails: ChannelDetails) {

    val context = LocalContext.current

    // Display a card with the video thumbnail, title, and channel name
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ){
        VideoThumbnailPlayImage(
            video = video,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f),
            onPlayClicked = {
                val intent = Intent(context, VideoPlayerActivity::class.java).apply {
                    putExtra("videoId", video.id)
                    flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                }
                context.startActivity(intent)
            },
            imageType = "high",
        )
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

@Preview
@Composable
fun HomeVideoCardPreview() {
    HomeVideoCard(
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
        channelDetails = ChannelDetails(
            id = "1",
            title = "Channel Title",
            description = "Channel Description",
            publishedAt = "2021-01-01T00:00:00Z",
            thumbnailDefaultUrl = "https://via.placeholder.com/150",
            thumbnailDefaultWidth = 150,
            thumbnailDefaultHeight = 150,
            thumbnailMediumUrl = "https://via.placeholder.com/150",
            thumbnailMediumWidth = 150,
            thumbnailMediumHeight = 150,
            thumbnailHighUrl = "https://via.placeholder.com/150",
            thumbnailHighWidth = 150,
            thumbnailHighHeight = 150,
            viewCount = "1000",
            subscriberCount = "1000",
            hiddenSubscriberCount = false,
            videoCount = "1000",
            likesPlaylistId = "1",
            uploadsPlaylistId = "1",
        )
    )
}