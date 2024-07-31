package com.akoscz.youtubechannels.data.models.api

/**
 * Data classes for parsing JSON responses from the YouTube Data API
 * YoutubeApiService.getPlaylistItems(playlistId: String) endpoint.
 */
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