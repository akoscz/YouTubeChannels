package com.akoscz.youtubechannels.data.models.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.akoscz.youtubechannels.data.models.api.ChannelDetailsItem
import java.util.Locale

/**
 * ChannelDetails are stored in the "channel_details" table.
 *
 * The "id" column in the "channel_details" table references the "id" column in the "channels" table.
 *
 */
@Entity(tableName = "channel_details")
data class ChannelDetails (
    @PrimaryKey val id: String, // This should match the Channel's id
    val title: String,
    val description: String,
    val customUrl: String? = null,
    val publishedAt: String,
    val thumbnailDefaultUrl: String,
    val thumbnailDefaultWidth: Int,
    val thumbnailDefaultHeight: Int,
    val thumbnailMediumUrl: String,
    val thumbnailMediumWidth: Int,
    val thumbnailMediumHeight: Int,
    val thumbnailHighUrl: String,
    val thumbnailHighWidth: Int,
    val thumbnailHighHeight: Int,
    val viewCount: String,
    val subscriberCount: String,
    val hiddenSubscriberCount: Boolean,
    val videoCount: String,
    val likesPlaylistId: String,
    val uploadsPlaylistId: String,
    val bannerExternalUrl: String? = null
)

/**
 * Helper function to map a ChannelDetailsItem to a ChannelDetails entity.
 */
fun mapToChannelDetails(channelDetailsItem: ChannelDetailsItem): ChannelDetails {
    return ChannelDetails(
        id = channelDetailsItem.id,
        title = channelDetailsItem.snippet.title,
        description = channelDetailsItem.snippet.description,
        customUrl = channelDetailsItem.snippet.customUrl,
        publishedAt = channelDetailsItem.snippet.publishedAt,
        thumbnailDefaultUrl = channelDetailsItem.snippet.thumbnails.default.url,
        thumbnailDefaultWidth = channelDetailsItem.snippet.thumbnails.default.width ?: 0,
        thumbnailDefaultHeight = channelDetailsItem.snippet.thumbnails.default.height ?: 0,
        thumbnailMediumUrl = channelDetailsItem.snippet.thumbnails.medium.url,
        thumbnailMediumWidth = channelDetailsItem.snippet.thumbnails.medium.width ?: 0,
        thumbnailMediumHeight = channelDetailsItem.snippet.thumbnails.medium.height ?: 0,
        thumbnailHighUrl = channelDetailsItem.snippet.thumbnails.high.url,
        thumbnailHighWidth = channelDetailsItem.snippet.thumbnails.high.width ?: 0,
        thumbnailHighHeight = channelDetailsItem.snippet.thumbnails.high.height ?: 0,
        viewCount = channelDetailsItem.statistics.viewCount,
        subscriberCount = channelDetailsItem.statistics.subscriberCount,
        hiddenSubscriberCount = channelDetailsItem.statistics.hiddenSubscriberCount,
        videoCount = channelDetailsItem.statistics.videoCount,
        likesPlaylistId = channelDetailsItem.contentDetails.relatedPlaylists.likes,
        uploadsPlaylistId = channelDetailsItem.contentDetails.relatedPlaylists.uploads,
        bannerExternalUrl = channelDetailsItem.brandingSettings.image?.bannerExternalUrl
    )
}

// Extension functions

/**
 * Helper function to get a modified banner URL based on the screen width.
 */
fun ChannelDetails.getModifiedBannerUrl(screenWidthDp: Int): String {
    val suffix = when {
        screenWidthDp <= 320 -> "=w320-fcrop64=1,32b75a57cd48a5a8-k-c0xffffffff-no-nd-rj"
        screenWidthDp <= 640 -> "=w640-fcrop64=1,32b75a57cd48a5a8-k-c0xffffffff-no-nd-rj"
        screenWidthDp <= 960 -> "=w960-fcrop64=1,32b75a57cd48a5a8-k-c0xffffffff-no-nd-rj"
        screenWidthDp <= 1280 -> "=w1280-fcrop64=1,32b75a57cd48a5a8-k-c0xffffffff-no-nd-rj"
        screenWidthDp <= 1440 -> "=w1440-fcrop64=1,32b75a57cd48a5a8-k-c0xffffffff-no-nd-rj"
        else -> "" // don't modify
    }
    return bannerExternalUrl + suffix
}

/**
 * Helper function to get a formatted view count.
 */
fun ChannelDetails.getFormattedViewCount(): String {
    return formatCount(viewCount.toLongOrNull())
}

/**
 * Helper function to get a formatted video count.
 */
fun ChannelDetails.getFormattedVideoCount(): String {
    return formatCount(videoCount.toLongOrNull())
}

/**
 * Helper function to get a formatted subscriber count.
 */
fun ChannelDetails.getFormattedSubscriberCount(): String {
    return formatCount(subscriberCount.toLongOrNull())
}

/**
 * Helper function to format a count.
 *
 * Numbers over 1 million are formatted as 1.2M.
 * Numbers over 1 thousand are formatted as 1.2K.
 * All other numbers are formatted as is.
 */
fun formatCount(count: Long?): String {
    return when {
        count == null -> ""
        count >= 1_000_000 -> String.format(Locale.getDefault(), "%.1fM", count / 1_000_000.0)
        count >= 1_000 -> String.format(Locale.getDefault(), "%.1fK", count / 1_000.0)
        else -> count.toString()
    }
}
