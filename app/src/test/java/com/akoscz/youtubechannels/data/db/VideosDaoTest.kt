package com.akoscz.youtubechannels.data.db

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.room.Room
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
import kotlin.collections.listOf as listOf

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33], manifest = Config.NONE)
class VideosDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var videosDao: VideosDao

    companion object {
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

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        videosDao = database.videosDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `insertVideos inserts video into database`() = runTest {
        val video = video1
        videosDao.insertVideos(listOf(video))

        val result = videosDao.getVideoById(video.id)
        assertEquals(video, result)
    }

    @Test
    fun `insertVideos inserts multiple videos into database`() = runTest {
        val videos = listOf(
            video1,
            video2
        )
        videosDao.insertVideos(videos)

        val result = listOf(
            videosDao.getVideoById(video1.id),
            videosDao.getVideoById(video2.id)
        )

        assertEquals(videos, result)
    }

    @Test
    fun `getVideoById returns correct video`() = runTest {
        val video = video1
        videosDao.insertVideos(listOf(video))

        val result = videosDao.getVideoById(video.id)
        assertEquals(video, result)
    }

    @Test
    fun `getVideoById returns null for non-existent video`() = runTest {
        val result = videosDao.getVideoById("nonexistentId")
        assertNull(result)
    }

    @Test
    fun `getNewestVideos returns newest videos`() = runTest {
        val videos = listOf(
            video1.copy(publishedAt = "2023-01-02"),
            video2.copy(publishedAt = "2023-01-01")
        )
        videosDao.insertVideos(videos)

        val result = videosDao.getNewestVideos(video1.channelId, 2)
        assertEquals(videos, result)
    }

    @Test
    fun `getPopularVideos returns popular videos`() = runTest {
        val videos = listOf(
            video1.copy(viewCount = 200),
            video2.copy(viewCount = 100)
        )
        videosDao.insertVideos(videos)

        val result = videosDao.getPopularVideos(video1.channelId, 2)
        assertEquals(videos, result)
    }

    @Test
    fun `getOldestVideos returns oldest videos`() = runTest {
        val videos = listOf(
            video1.copy(publishedAt = "2023-01-01"),
            video2.copy(publishedAt = "2023-01-02")
        )
        videosDao.insertVideos(videos)

        val result = videosDao.getOldestVideos(video1.channelId, 2)
        assertEquals(videos, result)
    }
}