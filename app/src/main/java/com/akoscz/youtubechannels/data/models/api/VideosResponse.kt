package com.akoscz.youtubechannels.data.models.api

/**
 * Data classes for parsing JSON responses from the YouTube Data API
 * YoutubeApiService.getVideos(videoIds: String) endpoint.
 */
data class VideosResponse(
    val kind: String,
    val etag: String,
    val pageInfo: PageInfo,
    val items: List<VideoItem>
)

data class VideoItem(
    val kind: String,
    val etag: String,
    val id: String,
    val snippet: VideoItemSnippet,
    val contentDetails: VideoItemContentDetails,
    val statistics: VideoItemStatistics,
    val player: VideoItemPlayer
)

data class VideoItemPlayer(
    val embedHtml: String
)

data class VideoItemStatistics(
    val viewCount: String,
    val likeCount: String,
    val favoriteCount: String,
    val commentCount: String
)

data class VideoItemContentDetails(
    val duration: String,
    val dimension: String,
    val definition: String,
    val caption: String,
    val licensedContent: Boolean,
    val contentRating: ContentRating,
    val projection: String
)

data class VideoItemSnippet(
    val publishedAt: String,
    val channelId: String,
    val title: String,
    val description: String,
    val thumbnails: Thumbnails,
    val channelTitle: String,
    val categoryId: String,
    val defaultLanguage: String,
    val liveBroadcastContent: String,
    val localized: Localized,
    val defaultAudioLanguage: String
)

