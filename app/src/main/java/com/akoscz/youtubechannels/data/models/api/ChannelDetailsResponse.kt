package com.akoscz.youtubechannels.data.models.api

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
    val contentDetails: ContentDetails,
    val statistics: Statistics,
    val brandingSettings: BrandingSettings
)

data class ChannelDetailsItemSnippet(
    val title: String,
    val description: String,
    val customUrl: String? = null,
    val publishedAt: String,
    val thumbnails: ChannelDetailsItemSnippetThumbnails,
    val localized: Localized,
    val country: String? = null
)

data class ChannelDetailsItemSnippetThumbnails(
    val default: ChannelDetailsItemSnippetThumbnail,
    val medium: ChannelDetailsItemSnippetThumbnail,
    val high: ChannelDetailsItemSnippetThumbnail
)

data class ChannelDetailsItemSnippetThumbnail(
    val url: String,
    val width: Int,
    val height: Int
)

data class Localized(
    val title: String,
    val description: String
)

data class ContentDetails(
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
    val image: ImageSettings
)

data class ChannelSettings(
    val title: String,
    val description: String,
    val keywords: String,
    val unsubscribedTrailer: String? = null,
    val country: String? = null
)

data class ImageSettings(
    val bannerExternalUrl: String
)