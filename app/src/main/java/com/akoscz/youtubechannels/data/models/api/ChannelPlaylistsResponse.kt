package com.akoscz.youtubechannels.data.models.api

data class ChannelPlaylistsResponse(
    val kind: String,
    val etag: String,
    val nextPageToken: String?,
    val pageInfo: PageInfo,
    val items: List<ChannelPlaylistsItem>
)

data class ChannelPlaylistsItem(
    val kind: String,
    val etag: String,
    val id: String,
    val snippet: ChannelPlaylistsItemSnippet,
    val contentDetails: ChannelPlaylistsItemContentDetails,
    val player: Player
)

data class ChannelPlaylistsItemSnippet(
    val publishedAt: String,
    val channelId: String,
    val title: String,
    val description: String,
    val thumbnails: ChannelPlaylistsItemSnippetThumbnails,
    val channelTitle: String,
    val localized: Localized
)

data class ChannelPlaylistsItemSnippetThumbnails(
    val default: Thumbnail,
    val medium: Thumbnail,
    val high: Thumbnail,
    val standard: Thumbnail?,
    val maxres: Thumbnail?
)

data class ChannelPlaylistsItemContentDetails(
    val itemCount: Int
)

