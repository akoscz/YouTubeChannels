package com.akoscz.youtubechannels.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.akoscz.youtubechannels.data.models.room.Playlist
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: Playlist)

    @Query("SELECT * FROM playlists WHERE channelId = :channelId AND title != 'Uploads'")
    fun getCustomPlaylists(channelId: String): Flow<List<Playlist>>

    @Query("SELECT * FROM playlists WHERE channelId = :channelId AND title = 'Uploads'")
    fun getUploadsPlaylist(channelId: String): Flow<Playlist?>
    
    suspend fun insertAll(playlists: List<Playlist>) {
        playlists.forEach {
            insertPlaylist(it)
        }
    }
}