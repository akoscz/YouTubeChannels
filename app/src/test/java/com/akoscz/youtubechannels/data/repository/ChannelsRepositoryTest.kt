package com.akoscz.youtubechannels.data.repository

import com.akoscz.youtubechannels.data.db.ChannelDetailsDao
import com.akoscz.youtubechannels.data.db.ChannelsDao
import com.akoscz.youtubechannels.data.db.PlaylistsDao
import com.akoscz.youtubechannels.data.db.VideosDao
import com.akoscz.youtubechannels.data.models.room.Channel
import com.akoscz.youtubechannels.data.models.room.ChannelDetails
import com.akoscz.youtubechannels.data.models.room.Video
import com.akoscz.youtubechannels.data.network.YoutubeApiService
import com.akoscz.youtubechannels.ui.viewmodels.SortType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class ChannelsRepositoryTest {

    @Mock
    private lateinit var youtubeApiService: YoutubeApiService

    @Mock
    private lateinit var channelsDao: ChannelsDao

    @Mock
    private lateinit var channelDetailsDao: ChannelDetailsDao

    @Mock
    private lateinit var playlistsDao: PlaylistsDao

    @Mock
    private lateinit var videosDao: VideosDao

    @InjectMocks
    private lateinit var channelsRepository: ChannelsRepository

    @Before
    fun setUp() {
        // Set up any common initialization here
    }

    companion object {
        val mockChannel1 = Channel(
            id = "1",
            title = "Channel 1",
            description = "Description 1",
            thumbnailDefaultUrl = "url1",
            thumbnailHighUrl = "url1_high",
            thumbnailMediumUrl = "url1_medium",
            channelDetailsId = "1"
        )

        val mockChannel2 = Channel(
            id = "2",
            title = "Channel 2",
            description = "Description 2",
            thumbnailDefaultUrl = "url2",
            thumbnailHighUrl = "url2_high",
            thumbnailMediumUrl = "url2_medium",
            channelDetailsId = "2"
        )

        val mockChannelDetails1 = ChannelDetails(
            id = "Channel1",
            title = "Title1",
            description = "Description1",
            customUrl = "customUrl1",
            publishedAt = "2023-01-01",
            thumbnailDefaultUrl = "url1",
            thumbnailDefaultWidth = 100,
            thumbnailDefaultHeight = 100,
            thumbnailMediumUrl = "url1",
            thumbnailMediumWidth = 100,
            thumbnailMediumHeight = 100,
            thumbnailHighUrl = "url1",
            thumbnailHighWidth = 100,
            thumbnailHighHeight = 100,
            viewCount = "1000",
            subscriberCount = "1000",
            hiddenSubscriberCount = false,
            videoCount = "1000",
            likesPlaylistId = "likesPlaylistId1",
            uploadsPlaylistId = "uploadsPlaylistId1",
            bannerExternalUrl = "bannerExternalUrl1"
        )

        val mockVideo1 = Video(
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
    }

    @Test
    fun `getFollowedChannels returns channels from dao`() = runTest {
        val mockChannels = listOf(
            mockChannel1,
            mockChannel2
        )
        `when`(channelsDao.getAllChannels()).thenReturn(flowOf(mockChannels))

        val result = channelsRepository.getFollowedChannels().firstOrNull()

        verify(channelsDao).getAllChannels()

        assertEquals(mockChannels, result)
    }

    @Test
    fun `unfollowChannel removes channel from dao`() = runTest {
        val channel = mockChannel1
        `when`(channelDetailsDao.getChannelDetails(channel.id)).thenReturn(null)

        channelsRepository.unfollowChannel(channel)

        verify(channelsDao).delete(channel)
        verify(channelDetailsDao).getChannelDetails(channel.id)
    }

    @Test
    fun `followChannel inserts channel into dao`() = runTest {
        val channel = mockChannel1

        channelsRepository.followChannel(channel)

        verify(channelsDao).insert(channel)
    }

    @Test
    fun `unfollowChannel removes channel and its details from dao`() = runTest {
        val channel = mockChannel1
        val channelDetails = mockChannelDetails1

        `when`(channelDetailsDao.getChannelDetails(channel.id)).thenReturn(channelDetails)

        channelsRepository.unfollowChannel(channel)

        verify(channelsDao).delete(channel)
        verify(channelDetailsDao).delete(channelDetails)
    }

    @Test
    fun `getFollowedChannels returns empty list when no channels are followed`() = runTest {
        `when`(channelsDao.getAllChannels()).thenReturn(flowOf(emptyList()))

        val result = channelsRepository.getFollowedChannels().firstOrNull()


        verify(channelsDao).getAllChannels()
        assertTrue(result.isNullOrEmpty())
    }

    @Test
    fun `getHomeVideos returns videos sorted by newest`() = runTest {
        val channel = mockChannel1

        val videos = listOf(
            mockVideo1.copy(
                id = "1",
                publishedAt = "2023-01-01"
            ),
            mockVideo1.copy(
                id = "2",
                publishedAt = "2023-01-02"
            ),
            mockVideo1.copy(
                id = "3",
                publishedAt = "2023-01-03"
            ),
            mockVideo1.copy(
                id = "4",
                publishedAt = "2023-01-04"
            ),
            mockVideo1.copy(
                id = "5",
                publishedAt = "2024-01-05"
            )
        )

        `when`(channelsDao.getAllChannels()).thenReturn(flowOf(listOf(channel)))
        `when`(
            videosDao.getNewestVideos(
                channel.id,
                5
            )
        ).thenReturn(videos.sortedByDescending { it.publishedAt })

        val result = channelsRepository.getHomeVideos(SortType.NEWEST, 5).firstOrNull()

        // verify that channelsDao.getAllChannels was called
        verify(channelsDao).getAllChannels()
        // verify that videosDao.getNewestVideos was called with the correct channelId and limit
        verify(videosDao).getNewestVideos(channel.id, 5)

        // assert that the videos are sorted by newest
        assertEquals(videos.sortedByDescending { it.publishedAt }, result)
    }

    @Test
    fun `getPlaylistVideos returns empty list when no videos are found`() = runTest {
        val playlistId = "playlist1"

        `when`(playlistsDao.getVideosFromPlaylist(playlistId)).thenReturn(flowOf(emptyList()))
        `when`(
            youtubeApiService.getPlaylistItems(
                playlistId = playlistId,
                pageToken = null,
                maxResults = 5
            )
        ).thenReturn(mock())

        val result = channelsRepository.getPlaylistVideos(playlistId, null, 5)

        verify(playlistsDao).getVideosFromPlaylist(playlistId)

        assertTrue(result.first.isEmpty())
        assertNull(result.second)
    }

    @Test
    fun `getChannelDetails returns null when channel details are not found`() = runTest {
        val channelId = "1"

        `when`(channelDetailsDao.getChannelDetails(channelId)).thenReturn(null)
        `when`(
            youtubeApiService.getChannelDetails(
                id = channelId
            )
        ).thenReturn(mock())

        val result = channelsRepository.getChannelDetails(channelId)

        verify(channelDetailsDao).getChannelDetails(channelId)
        verify(youtubeApiService).getChannelDetails(id = channelId)

        assertNull(result)
    }

    @Test
    fun `searchChannels returns empty list when no channels are found`() = runTest {
        val query = "nonexistent"

        `when`(
            youtubeApiService.searchChannels(
                query = query,
                pageToken = null,
                maxResults = 5
            )
        ).thenReturn(mock())

        val result = channelsRepository.searchChannels(query, null, 5)

        verify(youtubeApiService).searchChannels(query = query, pageToken = null, maxResults = 5)

        assertTrue(result.first.isEmpty())
        assertNull(result.second)
    }

    @Test
    fun `isFollowing returns true when channel is followed`() = runTest {
        val channel = mockChannel1

        `when`(channelsDao.isChannelFollowed(channel.id)).thenReturn(true)

        val result = channelsRepository.isFollowing(channel)

        verify(channelsDao).isChannelFollowed(channel.id)

        assertTrue(result)
    }

    @Test
    fun `getVideoById returns video from dao`() = runTest {
        val videoId = "1"
        val video = mockVideo1

        `when`(videosDao.getVideoById(videoId)).thenReturn(video)

        val result = channelsRepository.getVideoById(videoId)

        verify(videosDao).getVideoById(videoId)

        assertEquals(video, result)
    }
}