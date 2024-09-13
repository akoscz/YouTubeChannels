package com.akoscz.youtubechannels.data.db

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.akoscz.youtubechannels.data.models.room.Channel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33], manifest = Config.NONE)
class ChannelsDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var channelsDao: ChannelsDao
    private lateinit var context: Context

    @Before
    fun setUp() {
        // Use Robolectric's ApplicationProvider to get a context for local unit tests
        context = ApplicationProvider.getApplicationContext()

        database = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        channelsDao = database.channelDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `insert channel inserts channel into database`() = runTest {
        val channel = Channel(
            id = "1",
            title = "Channel 1",
            description = "Description 1",
            thumbnailDefaultUrl = "url1",
            thumbnailHighUrl = "url1_high",
            thumbnailMediumUrl = "url1_medium",
            channelDetailsId = "1"
        )

        channelsDao.insert(channel)

        val result = channelsDao.getAllChannels().firstOrNull()
        assertTrue(result?.contains(channel) == true)
    }

    @Test
    fun `delete channel removes channel from database`() = runTest {
        val channel = Channel(
            id = "1",
            title = "Channel 1",
            description = "Description 1",
            thumbnailDefaultUrl = "url1",
            thumbnailHighUrl = "url1_high",
            thumbnailMediumUrl = "url1_medium",
            channelDetailsId = "1"
        )

        channelsDao.insert(channel)
        channelsDao.delete(channel)

        val result = channelsDao.getAllChannels().firstOrNull()
        assertTrue(result?.contains(channel) == false)
    }

    @Test
    fun `get all channels returns all channels`() = runTest {
        val channel1 = Channel(
            id = "1",
            title = "Channel 1",
            description = "Description 1",
            thumbnailDefaultUrl = "url1",
            thumbnailHighUrl = "url1_high",
            thumbnailMediumUrl = "url1_medium",
            channelDetailsId = "1"
        )
        val channel2 = Channel(
            id = "2",
            title = "Channel 2",
            description = "Description 2",
            thumbnailDefaultUrl = "url2",
            thumbnailHighUrl = "url2_high",
            thumbnailMediumUrl = "url2_medium",
            channelDetailsId = "2"
        )

        channelsDao.insert(channel1)
        channelsDao.insert(channel2)

        val result = channelsDao.getAllChannels().firstOrNull()
        assertEquals(listOf(channel1, channel2), result)
    }

    @Test
    fun `update channel details id updates channel details id`() = runTest {
        val channel = Channel(
            id = "1",
            title = "Channel 1",
            description = "Description 1",
            thumbnailDefaultUrl = "url1",
            thumbnailHighUrl = "url1_high",
            thumbnailMediumUrl = "url1_medium",
            channelDetailsId = "1"
        )

        channelsDao.insert(channel)
        channelsDao.updateChannelDetailsId(channel.id, "newDetailsId")

        val result = channelsDao.getAllChannels().firstOrNull()?.find { it.id == channel.id }
        assertEquals("newDetailsId", result?.channelDetailsId)
    }

    @Test
    fun `is channel followed returns true when channel is followed`() = runTest {
        val channel = Channel(
            id = "1",
            title = "Channel 1",
            description = "Description 1",
            thumbnailDefaultUrl = "url1",
            thumbnailHighUrl = "url1_high",
            thumbnailMediumUrl = "url1_medium",
            channelDetailsId = "1"
        )

        channelsDao.insert(channel)

        val result = channelsDao.isChannelFollowed(channel.id)
        assertTrue(result)
    }

    @Test
    fun `is channel followed returns false when channel is not followed`() = runTest {
        val result = channelsDao.isChannelFollowed("nonexistentId")
        assertFalse(result)
    }
}