package com.akoscz.youtubechannels.ui.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.akoscz.youtubechannels.data.models.room.Channel
import com.akoscz.youtubechannels.data.repository.ChannelsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
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
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
@ExperimentalCoroutinesApi
class FollowedChannelsViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var channelsRepository: ChannelsRepository

    private lateinit var followedChannelsViewModel: FollowedChannelsViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is empty list`() = runTest {
        `when`(channelsRepository.getFollowedChannels()).thenReturn(flowOf(emptyList()))

        followedChannelsViewModel = FollowedChannelsViewModel(channelsRepository)

        Assert.assertTrue(followedChannelsViewModel.followedChannels.value.isEmpty())
    }

    @Test
    fun `followed channels are loaded correctly`() = runTest {
        val mockChannels = listOf(
            Channel(
                id = "1",
                title = "Channel 1",
                description = "Description 1",
                thumbnailDefaultUrl = "https://example.com/channel1.jpg",
                thumbnailHighUrl = "https://example.com/channel1_high.jpg",
                thumbnailMediumUrl = "https://example.com/channel1_medium.jpg",
                channelDetailsId = "1"
            ),
            Channel(
                id = "2",
                title = "Channel 2",
                description = "Description 2",
                thumbnailDefaultUrl = "https://example.com/channel2.jpg",
                thumbnailHighUrl = "https://example.com/channel2_high.jpg",
                thumbnailMediumUrl = "https://example.com/channel2_medium.jpg",
                channelDetailsId = "2"
            )
        )
        `when`(channelsRepository.getFollowedChannels()).thenReturn(flowOf(mockChannels))

        followedChannelsViewModel = FollowedChannelsViewModel(channelsRepository)

        val job = launch {
            followedChannelsViewModel.followedChannels.collect { channels ->
                Assert.assertEquals(mockChannels, channels)
            }
        }

        advanceUntilIdle()
        job.cancel()
    }

    @Test
    fun `unfollow channel removes channel from repository`() = runTest {
        val channel = Channel(
            id = "1",
            title = "Channel 1",
            description = "Description 1",
            thumbnailDefaultUrl = "https://example.com/channel1.jpg",
            thumbnailHighUrl = "https://example.com/channel1_high.jpg",
            thumbnailMediumUrl = "https://example.com/channel1_medium.jpg",
            channelDetailsId = "1"
        )
        followedChannelsViewModel = FollowedChannelsViewModel(channelsRepository)

        followedChannelsViewModel.unfollowChannel(channel)

        advanceUntilIdle()

        // Verify that the repository's unfollowChannel method was called
        verify(channelsRepository).unfollowChannel(channel)
    }
}