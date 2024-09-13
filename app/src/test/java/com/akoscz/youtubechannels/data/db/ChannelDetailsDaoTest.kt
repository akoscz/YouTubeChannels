package com.akoscz.youtubechannels.data.db

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.akoscz.youtubechannels.data.models.room.ChannelDetails
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33], manifest = Config.NONE)
class ChannelDetailsDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var channelDetailsDao: ChannelDetailsDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        channelDetailsDao = database.channelDetailsDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    companion object {
        val channelDetails1 = ChannelDetails(
            id = "1",
            title = "Channel Title",
            description = "Channel Description",
            publishedAt = "2021-01-01T00:00:00Z",
            thumbnailDefaultUrl = "https://via.placeholder.com/150",
            thumbnailDefaultWidth = 150,
            thumbnailDefaultHeight = 150,
            thumbnailMediumUrl = "https://via.placeholder.com/150",
            thumbnailMediumWidth = 150,
            thumbnailMediumHeight = 150,
            thumbnailHighUrl = "https://via.placeholder.com/150",
            thumbnailHighWidth = 150,
            thumbnailHighHeight = 150,
            viewCount = "1000",
            subscriberCount = "1000",
            hiddenSubscriberCount = false,
            videoCount = "1000",
            likesPlaylistId = "1",
            uploadsPlaylistId = "1",
        )

        val channelDetails2 = ChannelDetails(
            id = "2",
            title = "Channel Title 2",
            description = "Channel Description 2",
            publishedAt = "2021-01-02T00:00:00Z",
            thumbnailDefaultUrl = "https://via.placeholder.com/150",
            thumbnailDefaultWidth = 150,
            thumbnailDefaultHeight = 150,
            thumbnailMediumUrl = "https://via.placeholder.com/150",
            thumbnailMediumWidth = 150,
            thumbnailMediumHeight = 150,
            thumbnailHighUrl = "https://via.placeholder.com/150",
            thumbnailHighWidth = 150,
            thumbnailHighHeight = 150,
            viewCount = "2000",
            subscriberCount = "2000",
            hiddenSubscriberCount = false,
            videoCount = "2000",
            likesPlaylistId = "2",
            uploadsPlaylistId = "2",
        )
    }
    @Test
    fun `insert ChannelDetails inserts details into database`() = runTest {
        val channelDetails = channelDetails1

        channelDetailsDao.insert(channelDetails)

        val result = channelDetailsDao.getChannelDetails(channelDetails.id)
        assertEquals(channelDetails, result)
    }

    @Test
    fun `getChannelDetails returns correct details`() = runTest {
        channelDetailsDao.insert(channelDetails1)
        channelDetailsDao.insert(channelDetails2)

        val result1 = channelDetailsDao.getChannelDetails(channelDetails1.id)
        assertEquals(channelDetails1, result1)

        val result2 = channelDetailsDao.getChannelDetails(channelDetails2.id)
        assertEquals(channelDetails2, result2)
    }

    @Test
    fun `getChannelDetails returns null for non-existent details`() = runTest {
        val result = channelDetailsDao.getChannelDetails("nonexistentId")
        assertNull(result)
    }

    @Test
    fun `delete ChannelDetails deletes details from database`() = runTest {
        val channelDetails = channelDetails1
        channelDetailsDao.insert(channelDetails)

        channelDetailsDao.delete(channelDetails)

        val result = channelDetailsDao.getChannelDetails(channelDetails.id)
        assertNull(result)
    }
}