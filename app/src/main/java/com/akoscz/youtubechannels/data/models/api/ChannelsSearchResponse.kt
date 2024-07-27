package com.akoscz.youtubechannels.data.models.api

data class ChannelsSearchResponse(
    val kind: String,
    val etag: String,
    val nextPageToken: String?, // Can be null if there's no next page
    val regionCode: String,
    val pageInfo: PageInfo,
    val items: List<SearchItem>
)

data class SearchItem(
    val kind: String,
    val etag: String,
    val id: SearchItemId,
    val snippet: SearchItemSnippet
)

data class SearchItemId(
    val kind: String,
    val channelId: String
)

data class SearchItemSnippet(
    val publishedAt: String,
    val channelId: String,
    val title: String,
    val description: String,
    val thumbnails: SearchItemSnippetThumbnails,
    val channelTitle: String,
    val liveBroadcastContent: String,
    val publishTime: String
)

data class SearchItemSnippetThumbnails(
    val default: SearchItemSnippetThumbnail,
    val medium: SearchItemSnippetThumbnail,
    val high: SearchItemSnippetThumbnail
)

data class SearchItemSnippetThumbnail(
    val url: String
)