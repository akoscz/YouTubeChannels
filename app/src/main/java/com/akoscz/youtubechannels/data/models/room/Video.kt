package com.akoscz.youtubechannels.data.models.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.akoscz.youtubechannels.data.models.api.VideoItem
import java.time.Duration
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

/**
 * This class represents a video in the database and is stored in the "videos" table.
 */
@Entity(tableName = "videos")
data class Video(
    @PrimaryKey val id: String,
    val publishedAt: String,
    val channelId: String,
    val title: String,
    val description: String,
    val defaultThumbnailUrl: String,
    val defaultThumbnailWidth: Int,
    val defaultThumbnailHeight: Int,
    val mediumThumbnailUrl: String,
    val mediumThumbnailWidth: Int,
    val mediumThumbnailHeight: Int,
    val highThumbnailUrl: String,
    val highThumbnailWidth: Int,
    val highThumbnailHeight: Int,
    val standardThumbnailUrl: String?,
    val standardThumbnailWidth: Int?,
    val standardThumbnailHeight: Int?,
    val maxresThumbnailUrl: String?,
    val maxresThumbnailWidth: Int?,
    val maxresThumbnailHeight: Int?,
    val channelTitle: String,
    val defaultLanguage: String,
    val defaultedAudioLanguage: String,
    val duration: String,
    val contentYtRating: String,
    val viewCount: Long,
    val likeCount: Long,
    val favoriteCount: Long,
    val commentCount: Long,
    val embedHtml: String,
)

/**
 * Helper function to map a VideoItem to a Video entity.
 */
fun mapToVideo(videoItem: VideoItem): Video {
    return Video(
        id = videoItem.id,
        publishedAt = videoItem.snippet.publishedAt,
        channelId = videoItem.snippet.channelId,
        title = videoItem.snippet.title,
        description = videoItem.snippet.description,
        defaultThumbnailUrl = videoItem.snippet.thumbnails.default.url,
        defaultThumbnailWidth = videoItem.snippet.thumbnails.default.width ?: 0,
        defaultThumbnailHeight = videoItem.snippet.thumbnails.default.height ?: 0,
        mediumThumbnailUrl = videoItem.snippet.thumbnails.medium.url,
        mediumThumbnailWidth = videoItem.snippet.thumbnails.medium.width ?: 0,
        mediumThumbnailHeight = videoItem.snippet.thumbnails.medium.height ?: 0,
        highThumbnailUrl = videoItem.snippet.thumbnails.high.url,
        highThumbnailWidth = videoItem.snippet.thumbnails.high.width ?: 0,
        highThumbnailHeight = videoItem.snippet.thumbnails.high.height ?: 0,
        standardThumbnailUrl = videoItem.snippet.thumbnails.standard?.url,
        standardThumbnailWidth = videoItem.snippet.thumbnails.standard?.width ?: 0,
        standardThumbnailHeight = videoItem.snippet.thumbnails.standard?.height ?: 0,
        maxresThumbnailUrl = videoItem.snippet.thumbnails.maxres?.url,
        maxresThumbnailWidth = videoItem.snippet.thumbnails.maxres?.width ?: 0,
        maxresThumbnailHeight = videoItem.snippet.thumbnails.maxres?.height ?: 0,
        channelTitle = videoItem.snippet.channelTitle,
        defaultLanguage = videoItem.snippet.defaultLanguage,
        defaultedAudioLanguage = videoItem.snippet.defaultAudioLanguage,
        duration = videoItem.contentDetails.duration,
        contentYtRating = videoItem.contentDetails.contentRating.ytRating ?: "",
        viewCount = videoItem.statistics.viewCount.toLong(),
        likeCount = videoItem.statistics.likeCount.toLong(),
        favoriteCount = videoItem.statistics.favoriteCount.toLong(),
        commentCount = videoItem.statistics.commentCount.toLong(),
        embedHtml = videoItem.player.embedHtml
    )
}

/**
 * Extension function to Video class that formats the duration of the video in a human-readable format.
 *
 * hh:mm:ss format is used for videos longer than an hour.
 * mm:ss format is used for videos shorter than an hour.
 */
fun Video.formattedDuration(): String {
    return try {
        val duration = Duration.parse(this.duration)
        val hours = duration.toHours()
        val minutes = duration.toMinutes() % 60
        val seconds = duration.seconds % 60

        if (hours > 0) {
            String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        }
    } catch (e: Exception) {
        "" // Return an empty string if parsing fails
    }
}

/**
 * Format the view count of the video in a shortened format.
 */
fun Video.formattedViewCount(): String {
    return formatCount(viewCount)
}

/**
 * Format the like count of the video in a shortened format.
 */
fun Video.formattedLikeCount(): String {
    return formatCount(likeCount)
}

/**
 * Format the favorite count of the video in a shortened format.
 */
fun Video.formattedFavoriteCount(): String {
    return formatCount(favoriteCount)
}

/**
 * Format the comment count of the video in a shortened format.
 */
fun Video.formattedCommentCount(): String {
    return formatCount(commentCount)
}

/**
 * Formats the published date of the video in a human-readable format.
 * Returns the number of days, hours, minutes, or seconds since the video was published.
 * Special case is when the duration is < 14 days (2 weeks), it is formatted as "X days ago".
 */
fun Video.timeAgo(): String {
    val formatter = DateTimeFormatter.ISO_INSTANT.withZone(ZoneId.systemDefault())
    val givenDateTime = ZonedDateTime.parse(publishedAt, formatter)
    val now = ZonedDateTime.now()

    val differenceSeconds = ChronoUnit.SECONDS.between(givenDateTime, now)
    val differenceMinutes = ChronoUnit.MINUTES.between(givenDateTime, now)
    val differenceHours = ChronoUnit.HOURS.between(givenDateTime, now)
    val differenceDays = ChronoUnit.DAYS.between(givenDateTime, now)
    val differenceWeeks = ChronoUnit.WEEKS.between(givenDateTime, now)
    val differenceMonths = ChronoUnit.MONTHS.between(givenDateTime, now)
    val differenceYears = ChronoUnit.YEARS.between(givenDateTime, now)

    return when {
        differenceSeconds < 60 -> "$differenceSeconds seconds ago"
        differenceMinutes < 60 -> "$differenceMinutes minutes ago"
        differenceHours < 24 -> "$differenceHours hours ago"
        differenceDays < 14 -> "$differenceDays days ago" // < 2 weeks
        differenceWeeks < 4 -> "$differenceWeeks weeks ago" // 3-4 weeks
        differenceMonths < 12 -> "$differenceMonths months ago"
        else -> "$differenceYears years ago"
    }
}

/**
 * Checks if the video is a YouTube Short.
 * The duration of a YouTube Short is less than or equal to 60 seconds.
 */
fun Video.isYoutubeShort(): Boolean {
    return try {
        Duration.parse(duration).seconds <= 60
    } catch (e: Exception) {
        false // Handle potential parsing errors
    }
}