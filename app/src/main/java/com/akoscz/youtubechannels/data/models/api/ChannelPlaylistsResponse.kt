package com.akoscz.youtubechannels.data.models.api

data class ChannelPlaylistsResponse(
    val kind: String,
    val etag: String,
    val nextPageToken: String?,
    val pageInfo: PageInfo,
    val items: List<ChannelPlaylist>
)

data class ChannelPlaylist(
    val kind: String,
    val etag: String,
    val id: String,
    val snippet: ChannelPlaylistSnippet,
    val contentDetails: ChannelPlaylistContentDetails,
    val player: Player
)

data class ChannelPlaylistSnippet(
    val publishedAt: String,
    val channelId: String,
    val title: String,
    val description: String,
    val thumbnails: ChannelPlaylistSnippetThumbnails,
    val channelTitle: String,
    val localized: Localized
)

data class ChannelPlaylistSnippetThumbnails(
    val default: Thumbnail,
    val medium: Thumbnail,
    val high: Thumbnail,
    val standard: Thumbnail?,
    val maxres: Thumbnail?
)

data class ChannelPlaylistContentDetails(
    val itemCount: Int
)
