package com.akoscz.youtubechannels.data.models.api

/**
 * Data classes for parsing JSON responses from the YouTube Data API
 * YoutubeApiService.searchChannels(query: String) endpoint.
 */
data class ChannelsSearchResponse(
    val kind: String,
    val etag: String,
    val nextPageToken: String?, // Can be null if there's no next page
    val regionCode: String,
    val pageInfo: PageInfo,
    val items: List<ChannelSearchItem>
)

data class ChannelSearchItem(
    val kind: String,
    val etag: String,
    val id: ChannelSearchItemId,
    val snippet: ChannelSearchItemSnippet
)

data class ChannelSearchItemId(
    val kind: String,
    val channelId: String
)

data class ChannelSearchItemSnippet(
    val publishedAt: String,
    val channelId: String,
    val title: String,
    val description: String,
    val thumbnails: ChannelSearchItemSnippetThumbnails,
    val channelTitle: String,
    val liveBroadcastContent: String,
    val publishTime: String
)

data class ChannelSearchItemSnippetThumbnails(
    val default: SearchItemSnippetThumbnail,
    val medium: SearchItemSnippetThumbnail,
    val high: SearchItemSnippetThumbnail
)

data class SearchItemSnippetThumbnail(
    val url: String
)