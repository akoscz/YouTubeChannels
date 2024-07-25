package com.akoscz.youtubechannels.data.models.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class ChannelDetails (
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
    val bannerExternalUrl: String
)

// Extension function for ChannelDetails
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

fun ChannelDetails.getFormattedViewCount(): String {
    return formatCount(viewCount.toLongOrNull())
}

fun ChannelDetails.getFormattedVideoCount(): String {
    return formatCount(videoCount.toLongOrNull())
}

fun ChannelDetails.getFormattedSubscriberCount(): String {
    return formatCount(subscriberCount.toLongOrNull())
}

private fun formatCount(count: Long?): String {
    return when {
        count == null -> ""
        count >= 1_000_000 -> String.format("%.1fM", count / 1_000_000.0)
        count >= 1_000 -> String.format("%.1fK", count / 1_000.0)
        else -> count.toString()
    }
}