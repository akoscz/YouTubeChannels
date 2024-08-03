package com.akoscz.youtubechannels.data.db

import androidx.room.Entity

@Entity(
    tableName = "playlists_videos",
    primaryKeys = ["playlistId", "videoId"])
data class PlaylistVideoCrossRef(
    val playlistId: String,
    val videoId: String
)