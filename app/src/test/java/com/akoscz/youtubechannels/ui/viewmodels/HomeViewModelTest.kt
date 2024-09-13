package com.akoscz.youtubechannels.ui.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.akoscz.youtubechannels.data.models.room.ChannelDetails
import com.akoscz.youtubechannels.data.models.room.Video
import com.akoscz.youtubechannels.data.repository.ChannelsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
@ExperimentalCoroutinesApi
class HomeViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var channelsRepository: ChannelsRepository

    private lateinit var homeViewModel: HomeViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        homeViewModel = HomeViewModel(channelsRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test initial state`() {
        Assert.assertTrue(homeViewModel.homeVideos.value.isEmpty())
        Assert.assertTrue(homeViewModel.channelDetailsMap.value.isEmpty())
        Assert.assertEquals(SortType.NEWEST, homeViewModel.sortType.value)
    }

    @Test
    fun `test fetchHomeVideos updates homeVideos and channelDetailsMap`() = runTest {
        val mockVideos = listOf(
            Video(
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
            ),
            Video(
                id = "2",
                publishedAt = "2023-01-01",
                channelId = "Channel2",
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
                channelTitle = "Channel2",
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
        )
        val mockChannelDetails = ChannelDetails(
            id = "Channel1",
            title = "Title1",
            description = "Description1",
            customUrl = "customUrl",
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
            likesPlaylistId = "likesPlaylistId",
            uploadsPlaylistId = "uploadsPlaylistId",
            bannerExternalUrl = "bannerExternalUrl"
        )

        `when`(channelsRepository.getHomeVideos(SortType.NEWEST, 5)).thenReturn(flowOf(mockVideos))
        `when`(channelsRepository.getChannelDetails("Channel1")).thenReturn(mockChannelDetails)
        `when`(channelsRepository.getChannelDetails("Channel2")).thenReturn(null)

        homeViewModel.fetchHomeVideos()

        advanceUntilIdle()

        Assert.assertEquals(mockVideos, homeViewModel.homeVideos.value)
        Assert.assertTrue(homeViewModel.channelDetailsMap.value.containsKey("Channel1"))
        Assert.assertEquals(mockChannelDetails, homeViewModel.channelDetailsMap.value["Channel1"])
    }

    @Test
    fun `test fetchHomeVideos with empty response`() = runTest {
        `when`(channelsRepository.getHomeVideos(SortType.NEWEST, 5)).thenReturn(flowOf(emptyList()))

        homeViewModel.fetchHomeVideos()

        advanceUntilIdle()

        Assert.assertTrue(homeViewModel.homeVideos.value.isEmpty())
        Assert.assertTrue(homeViewModel.channelDetailsMap.value.isEmpty())
    }

    @Test
    fun `test updateSortType updates sortType and fetches homeVideos`() = runTest {
        val mockVideos = listOf(
            Video(
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
        )

        `when`(channelsRepository.getHomeVideos(SortType.POPULAR, 5)).thenReturn(flowOf(mockVideos))

        homeViewModel.updateSortType(SortType.POPULAR)

        advanceUntilIdle()

        Assert.assertEquals(SortType.POPULAR, homeViewModel.sortType.value)
        Assert.assertEquals(mockVideos, homeViewModel.homeVideos.value)
    }

    @Test
    fun `test updateSortType with same sort type`() = runTest {
        val initialVideos = listOf(
            Video(
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
        )

        homeViewModel.setHomeVideos(initialVideos)

        `when`(
            channelsRepository.getHomeVideos(
                SortType.NEWEST,
                5
            )
        ).thenReturn(flowOf(initialVideos))

        homeViewModel.updateSortType(SortType.NEWEST)

        advanceUntilIdle()

        Assert.assertEquals(SortType.NEWEST, homeViewModel.sortType.value)
        Assert.assertEquals(initialVideos, homeViewModel.homeVideos.value)
    }
}