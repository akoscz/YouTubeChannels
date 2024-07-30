package com.akoscz.youtubechannels.data.models.api

data class ChannelPlaylistItemsResponse(
    val kind: String,
    val etag: String,
    val nextPageToken: String,
    val pageInfo: PageInfo,
    val items: List<ChannelPlaylistItem>
)

data class ChannelPlaylistItem(
    val contentDetails: ChannelPlaylistItemContentDetails
)

data class ChannelPlaylistItemContentDetails(
    val videoId: String
)