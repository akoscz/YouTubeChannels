package com.akoscz.youtubechannels.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.akoscz.youtubechannels.data.models.room.Playlist
import com.akoscz.youtubechannels.data.models.room.Video
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: Playlist)
    suspend fun insertAll(playlists: List<Playlist>) {
        playlists.forEach {
            insertPlaylist(it)
        }
    }

    @Query("SELECT * FROM playlists WHERE channelId = :channelId AND title != 'Uploads'")
    fun getCustomPlaylists(channelId: String): Flow<List<Playlist>>

    @Query("SELECT * FROM playlists WHERE channelId = :channelId AND title = 'Uploads'")
    fun getUploadsPlaylist(channelId: String): Flow<Playlist?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertVideos(videosList: List<Video>)

    @Query("SELECT * FROM videos WHERE id IN (SELECT videoId FROM playlists_videos WHERE playlistId = :playlistId)")
    fun getVideosFromPlaylist(playlistId: String): Flow<List<Video>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylistVideoCrossRef(crossRef: PlaylistVideoCrossRef)

}