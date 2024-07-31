package com.akoscz.youtubechannels.data.models.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.akoscz.youtubechannels.data.models.api.ChannelPlaylist

/**
 * This class represents a playlist in the database and are stored in the "playlists" table.
 */
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

/**
 * Helper function to create a playlist with the given parameters.
 */
fun uploadsPlaylist(id: String, channelId: String, title: String, description: String): Playlist {
    return Playlist(
        id = id,
        publishedAt = "",
        channelId = channelId,
        title = title,
        description = description,
        defaultThumbnailUrl = "",
        defaultThumbnailWidth = 0,
        defaultThumbnailHeight = 0,
        mediumThumbnailUrl = "",
        mediumThumbnailWidth = 0,
        mediumThumbnailHeight = 0,
        highThumbnailUrl = "",
        highThumbnailWidth = 0,
        highThumbnailHeight = 0,
        standardThumbnailUrl = null,
        standardThumbnailWidth = null,
        standardThumbnailHeight = null,
        maxresThumbnailUrl = null,
        maxresThumbnailWidth = null,
        maxresThumbnailHeight = null,
        itemCount = 0,
        embedHtml = ""
    )
}

/**
 * Helper function to map a ChannelPlaylist to a Playlist entity.
 */
fun mapToPlaylist(channelPlaylist: ChannelPlaylist): Playlist {
    return Playlist(
        id = channelPlaylist.id,
        publishedAt = channelPlaylist.snippet.publishedAt,
        channelId = channelPlaylist.snippet.channelId,
        title = channelPlaylist.snippet.title,
        description = channelPlaylist.snippet.description,
        defaultThumbnailUrl = channelPlaylist.snippet.thumbnails.default.url,
        defaultThumbnailWidth = channelPlaylist.snippet.thumbnails.default.width ?: 0,
        defaultThumbnailHeight = channelPlaylist.snippet.thumbnails.default.height ?: 0,
        mediumThumbnailUrl = channelPlaylist.snippet.thumbnails.medium.url,
        mediumThumbnailWidth = channelPlaylist.snippet.thumbnails.medium.width ?: 0,
        mediumThumbnailHeight = channelPlaylist.snippet.thumbnails.medium.height ?: 0,
        highThumbnailUrl = channelPlaylist.snippet.thumbnails.high.url,
        highThumbnailWidth = channelPlaylist.snippet.thumbnails.high.width ?: 0,
        highThumbnailHeight = channelPlaylist.snippet.thumbnails.high.height ?: 0,
        standardThumbnailUrl = channelPlaylist.snippet.thumbnails.standard?.url,
        standardThumbnailWidth = channelPlaylist.snippet.thumbnails.standard?.width ?: 0,
        standardThumbnailHeight = channelPlaylist.snippet.thumbnails.standard?.height ?: 0,
        maxresThumbnailUrl = channelPlaylist.snippet.thumbnails.maxres?.url,
        maxresThumbnailWidth = channelPlaylist.snippet.thumbnails.maxres?.width ?: 0,
        maxresThumbnailHeight = channelPlaylist.snippet.thumbnails.maxres?.height ?: 0,
        itemCount = channelPlaylist.contentDetails.itemCount,
        embedHtml = channelPlaylist.player.embedHtml
    )
}
