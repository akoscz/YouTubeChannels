package com.akoscz.youtubechannels.data.models

data class PageInfo(
    val totalResults: Int,
    val resultsPerPage: Int
)

data class SearchItem(
    val kind: String,
    val etag: String,
    val id: Id,
    val snippet: Snippet
)

data class Id(
    val kind: String,
    val channelId: String
)

data class Snippet(
    val publishedAt: String,
    val channelId: String,
    val title: String,
    val description: String,
    val thumbnails: Thumbnails,
    val channelTitle: String,
    val liveBroadcastContent: String,
    val publishTime: String
)

data class Thumbnails(
    val default: Thumbnail,
    val medium: Thumbnail,
    val high: Thumbnail
)

data class Thumbnail(
    val url: String
)

data class ChannelsSearchResponse(
    val kind: String,
    val etag: String,
    val nextPageToken: String?, // Can be null if there's no next page
    val regionCode: String,
    val pageInfo: PageInfo,
    val items: List<SearchItem>
)