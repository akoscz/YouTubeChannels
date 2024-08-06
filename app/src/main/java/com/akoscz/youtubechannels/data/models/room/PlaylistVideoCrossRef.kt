package com.akoscz.youtubechannels.data.models.room

import androidx.room.Entity

/**
 * This class represents the relationship between playlists and videos in the database and are stored in the "playlists_videos" table.
 */
@Entity(
    tableName = "playlists_videos",
    primaryKeys = ["playlistId", "videoId"])
data class PlaylistVideoCrossRef(
    val playlistId: String,
    val videoId: String
)