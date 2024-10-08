package com.akoscz.youtubechannels.data.models.api

/**
 * Data classes for parsing JSON responses from the YouTube Data API
 * YoutubeApiService.getChannelDetails(channelId: String) endpoint.
 */
data class ChannelDetailsResponse(
    val kind: String,
    val etag: String,
    val pageInfo: PageInfo,
    val items: List<ChannelDetailsItem>
)

data class ChannelDetailsItem(
    val kind: String,
    val etag: String,
    val id: String,
    val snippet: ChannelDetailsItemSnippet,
    val contentDetails: ChannelDetailsItemContentDetails,
    val statistics: Statistics,
    val brandingSettings: BrandingSettings
)

data class ChannelDetailsItemSnippet(
    val title: String,
    val description: String,
    val customUrl: String? = null,
    val publishedAt: String,
    val thumbnails: Thumbnails,
    val localized: Localized,
    val country: String? = null
)

data class ChannelDetailsItemContentDetails(
    val relatedPlaylists: RelatedPlaylists
)

data class RelatedPlaylists(
    val likes: String,
    val uploads: String
)

data class Statistics(
    val viewCount: String,
    val subscriberCount: String,
    val hiddenSubscriberCount: Boolean,
    val videoCount: String
)

data class BrandingSettings(
    val channel: ChannelSettings,
    val image: ImageSettings? = null,
)

data class ChannelSettings(
    val title: String,
    val description: String? = null,
    val keywords: String? = null,
    val unsubscribedTrailer: String? = null,
    val country: String? = null
)

data class ImageSettings(
    val bannerExternalUrl: String
)