package com.akoscz.youtubechannels.data.models.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.akoscz.youtubechannels.data.models.api.ChannelPlaylistsItem

@Entity(tableName = "playlists")
data class Playlist(
    @PrimaryKey val id: String,
    val publishedAt: String,
    val channelId: String,
    val title: String,
    val description: String,
    val defaultThumbnailUrl: String,
    val defaultThumbnailWidth: Int,
    val defaultThumbnailHeight: Int,
    val mediumThumbnailUrl: String,
    val mediumThumbnailWidth: Int,
    val mediumThumbnailHeight: Int,
    val highThumbnailUrl: String,
    val highThumbnailWidth: Int,
    val highThumbnailHeight: Int,
    val standardThumbnailUrl: String?,
    val standardThumbnailWidth: Int?,
    val standardThumbnailHeight: Int?,
    val maxresThumbnailUrl: String?,
    val maxresThumbnailWidth: Int?,
    val maxresThumbnailHeight: Int?,
    val itemCount: Int,
    val embedHtml: String
)

fun mapPlaylistsItem(playlistsItem: ChannelPlaylistsItem): Playlist {
    return Playlist(
        id = playlistsItem.id,
        publishedAt = playlistsItem.snippet.publishedAt,
        channelId = playlistsItem.snippet.channelId,
        title = playlistsItem.snippet.title,
        description = playlistsItem.snippet.description,
        defaultThumbnailUrl = playlistsItem.snippet.thumbnails.default.url,
        defaultThumbnailWidth = playlistsItem.snippet.thumbnails.default.width,
        defaultThumbnailHeight = playlistsItem.snippet.thumbnails.default.height,
        mediumThumbnailUrl = playlistsItem.snippet.thumbnails.medium.url,
        mediumThumbnailWidth = playlistsItem.snippet.thumbnails.medium.width,
        mediumThumbnailHeight = playlistsItem.snippet.thumbnails.medium.height,
        highThumbnailUrl = playlistsItem.snippet.thumbnails.high.url,
        highThumbnailWidth = playlistsItem.snippet.thumbnails.high.width,
        highThumbnailHeight = playlistsItem.snippet.thumbnails.high.height,
        standardThumbnailUrl = playlistsItem.snippet.thumbnails.standard?.url,
        standardThumbnailWidth = playlistsItem.snippet.thumbnails.standard?.width,
        standardThumbnailHeight = playlistsItem.snippet.thumbnails.standard?.height,
        maxresThumbnailUrl = playlistsItem.snippet.thumbnails.maxres?.url,
        maxresThumbnailWidth = playlistsItem.snippet.thumbnails.maxres?.width,
        maxresThumbnailHeight = playlistsItem.snippet.thumbnails.maxres?.height,
        itemCount = playlistsItem.contentDetails.itemCount,
        embedHtml = playlistsItem.player.embedHtml
    )
}
