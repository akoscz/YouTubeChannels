package com.akoscz.youtubechannels.data.db

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.room.Room
import com.akoscz.youtubechannels.data.models.room.Playlist
import com.akoscz.youtubechannels.data.models.room.PlaylistVideoCrossRef
import com.akoscz.youtubechannels.data.models.room.Video
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33], manifest = Config.NONE)
class PlaylistsDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var playlistsDao: PlaylistsDao
    private lateinit var videosDao: VideosDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        playlistsDao = database.playlistsDao()
        videosDao = database.videosDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    companion object {
        val playlist1 = Playlist(
            id = "1",
            channelId = "channel1",
            publishedAt = "2021-01-01",
            title = "Playlist 1",
            description = "Description 1",
            defaultThumbnailUrl = "url1",
            defaultThumbnailWidth = 0,
            defaultThumbnailHeight = 0,
            mediumThumbnailUrl = "url1_medium",
            mediumThumbnailWidth = 0,
            mediumThumbnailHeight = 0,
            highThumbnailUrl = "url1_high",
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

        val playlist2 = Playlist(
            id = "2",
            channelId = "channel1",
            publishedAt = "2021-01-01",
            title = "Playlist 2",
            description = "Description 2",
            defaultThumbnailUrl = "url2",
            defaultThumbnailWidth = 0,
            defaultThumbnailHeight = 0,
            mediumThumbnailUrl = "url2_medium",
            mediumThumbnailWidth = 0,
            mediumThumbnailHeight = 0,
            highThumbnailUrl = "url2_high",
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
        val video1 = Video(
            id = "1",
            publishedAt = "2023-01-01",
            channelId = "Channel1",
            title = "Title1",
            description = "Description1",
            defaultThumbnailUrl = "url1",
            defaultThumbnailWidth = 100,
            defaultThumbnailHeight = 100,
            mediumThumbnailUrl = "url1",
            mediumThumbnailWidth = 100,
            mediumThumbnailHeight = 100,
            highThumbnailUrl = "url1",
            highThumbnailWidth = 100,
            highThumbnailHeight = 100,
            standardThumbnailUrl = null,
            standardThumbnailWidth = null,
            standardThumbnailHeight = null,
            maxresThumbnailUrl = null,
            maxresThumbnailWidth = null,
            maxresThumbnailHeight = null,
            channelTitle = "Channel1",
            defaultLanguage = "en",
            defaultedAudioLanguage = "en",
            duration = "PT1H1M1S",
            contentYtRating = "ytRating",
            viewCount = 1000,
            likeCount = 1000,
            favoriteCount = 1000,
            commentCount = 1000,
            embedHtml = "embedHtml"
        )

        val video2 = Video(
            id = "2",
            publishedAt = "2023-01-01",
            channelId = "Channel1",
            title = "Title2",
            description = "Description2",
            defaultThumbnailUrl = "url2",
            defaultThumbnailWidth = 100,
            defaultThumbnailHeight = 100,
            mediumThumbnailUrl = "url2",
            mediumThumbnailWidth = 100,
            mediumThumbnailHeight = 100,
            highThumbnailUrl = "url2",
            highThumbnailWidth = 100,
            highThumbnailHeight = 100,
            standardThumbnailUrl = null,
            standardThumbnailWidth = null,
            standardThumbnailHeight = null,
            maxresThumbnailUrl = null,
            maxresThumbnailWidth = null,
            maxresThumbnailHeight = null,
            channelTitle = "Channel1",
            defaultLanguage = "en",
            defaultedAudioLanguage = "en",
            duration = "PT1H1M1S",
            contentYtRating = "ytRating",
            viewCount = 1000,
            likeCount = 1000,
            favoriteCount = 1000,
            commentCount = 1000,
            embedHtml = "embedHtml"
        )
    }

    @Test
    fun `insertPlaylist inserts playlist into database`() = runTest {
        val playlist = playlist1

        playlistsDao.insertPlaylist(playlist)

        val result = playlistsDao.getCustomPlaylists("channel1").firstOrNull()
        assertTrue(result?.contains(playlist) == true)
    }

    @Test
    fun `insertAll inserts multiple playlists into database`() = runTest {
        val playlists = listOf(
            playlist1,
            playlist2
        )
        playlistsDao.insertAll(playlists)

        val result = playlistsDao.getCustomPlaylists("channel1").firstOrNull()
        assertEquals(playlists, result)
    }

    @Test
    fun `getCustomPlaylists returns custom playlists`() = runTest {
        val playlist = playlist1
        playlistsDao.insertPlaylist(playlist)

        val result = playlistsDao.getCustomPlaylists("channel1").firstOrNull()
        assertTrue(result?.contains(playlist) == true)
    }

    @Test
    fun `getUploadsPlaylist returns uploads playlist`() = runTest {
        val playlist = playlist1.copy(
            id = "10",
            channelId = "channel1",
            title = "Uploads"
        )
        playlistsDao.insertPlaylist(playlist)

        val result = playlistsDao.getUploadsPlaylist("channel1").firstOrNull()
        assertEquals(playlist, result)
    }

    @Test
    fun `getVideosFromPlaylist returns videos from playlist`() = runTest {
        val playlist = playlist1
        val video = video1
        val crossRef = PlaylistVideoCrossRef(playlistId = "1", videoId = "1")

        playlistsDao.insertPlaylist(playlist)
        videosDao.insertVideos(listOf(video))
        playlistsDao.insertPlaylistVideoCrossRef(crossRef)

        val result = playlistsDao.getVideosFromPlaylist("1").firstOrNull()
        assertTrue(result?.contains(video) == true)
    }
}