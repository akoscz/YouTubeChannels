package com.akoscz.youtubechannels.ui.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.testing.asSnapshot
import com.akoscz.youtubechannels.data.models.room.Channel
import com.akoscz.youtubechannels.data.repository.ChannelsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doSuspendableAnswer

@RunWith(MockitoJUnitRunner::class)
class SearchChannelsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val channelsRepository = mock(ChannelsRepository::class.java)
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: SearchChannelsViewModel

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = SearchChannelsViewModel(channelsRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    companion object {
        val channel1 = Channel(
            id = "1",
            title = "Channel 1",
            description = "Description 1",
            thumbnailDefaultUrl = "https://example.com/channel1.jpg",
            thumbnailHighUrl = "https://example.com/channel1_high.jpg",
            thumbnailMediumUrl = "https://example.com/channel1_medium.jpg",
            channelDetailsId = "1"
        )

        val channel2 = Channel(
            id = "2",
            title = "Channel 2",
            description = "Description 2",
            thumbnailDefaultUrl = "https://example.com/channel2.jpg",
            thumbnailHighUrl = "https://example.com/channel2_high.jpg",
            thumbnailMediumUrl = "https://example.com/channel2_medium.jpg",
            channelDetailsId = "2"
        )
    }

    // Success Path Tests
    @Test
    fun `searchChannels with non-empty query returns results`() = runTest {
        val query = "test"
        val mockChannels = listOf(
            channel1
        )

        // Create a mock PagingSource
        val mockPagingSource = object : PagingSource<Int, Channel>() {
            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Channel> {
                return LoadResult.Page(data = mockChannels, prevKey = null, nextKey = null)
            }

            override fun getRefreshKey(state: PagingState<Int, Channel>): Int? = null
        }

        // Mock the Pager to use the mock PagingSource
        val mockPager = Pager(config = PagingConfig(pageSize = 10), pagingSourceFactory = { mockPagingSource })

        // Inject the mock Pager into the ViewModel
        viewModel.setPager(mockPager)

        // Call the searchChannels method in the ViewModel
        viewModel.searchChannels(query)
        val result = viewModel.searchResults.asSnapshot()

        // Verify the results
        result.map { channel -> assertEquals(mockChannels[0], channel) }
    }

    @Test
    fun `updateSearchQuery updates search query`() = runTest {
        val newQuery = "new query"

        // Update the search query in the ViewModel
        viewModel.updateSearchQuery(newQuery)

        // Retrieve the updated search query
        val result = viewModel.searchQuery.first()

        // Verify the search query is updated correctly
        assertEquals(newQuery, result)
    }

    @Test
    fun `followChannel follows a channel`() = runTest {
        // Mock the followChannel method in the repository
        `when`(channelsRepository.followChannel(channel1)).doSuspendableAnswer { Unit }

        // Call the followChannel method in the ViewModel
        viewModel.followChannel(channel1)

        // Advance the scheduler to ensure all coroutines have completed
        testScheduler.advanceUntilIdle()

        // Retrieve the following channels
        val result = viewModel.followingChannels.first()

        // Verify the channel is followed
        assertTrue(result[channel1.id] == true)
    }

    @Test
    fun `checkFollowingStatus checks following status`() = runTest {
        val channels = listOf(
            channel1
        )

        // Mock the isFollowing method in the repository
        `when`(channelsRepository.isFollowing(channel1)).doSuspendableAnswer { true }

        // Call the checkFollowingStatus method in the ViewModel
        viewModel.checkFollowingStatus(channels)

        // Advance the scheduler to ensure all coroutines have completed
        testScheduler.advanceUntilIdle()

        // Retrieve the following channels
        val result = viewModel.followingChannels.first()

        // Verify the channel is followed
        assertTrue(result[channel1.id] == true)
    }

    // Error Path Tests
    @Test
    fun `searchChannels with empty query returns empty results`() = runTest {
        val query = ""
        val mockChannels = emptyList<Channel>()

        // Create a mock PagingSource
        val mockPagingSource = object : PagingSource<Int, Channel>() {
            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Channel> {
                return LoadResult.Page(data = mockChannels, prevKey = null, nextKey = null)
            }

            override fun getRefreshKey(state: PagingState<Int, Channel>): Int? = null
        }

        // Mock the Pager to use the mock PagingSource
        val mockPager = Pager(config = PagingConfig(pageSize = 10), pagingSourceFactory = { mockPagingSource })

        // Inject the mock Pager into the ViewModel
        viewModel.setPager(mockPager)

        // Call the searchChannels method in the ViewModel
        viewModel.searchChannels(query)
        val result = viewModel.searchResults.asSnapshot()

        // Verify the results are empty
        assertTrue(result.isEmpty())
    }

    @Test
    fun `followChannel already followed does not duplicate`() = runTest {
        // Mock the followChannel method in the repository
        `when`(channelsRepository.followChannel(channel1)).doSuspendableAnswer { Unit }

        // Call the followChannel method in the ViewModel
        viewModel.followChannel(channel1)

        // Advance the scheduler to ensure all coroutines have completed
        testScheduler.advanceUntilIdle()

        // Follow the channel again
        viewModel.followChannel(channel1)
        testScheduler.advanceUntilIdle()

        // Retrieve the following channels
        val result = viewModel.followingChannels.first()

        // Verify the channel is followed
        assertTrue(result[channel1.id] == true)
    }

    @Test
    fun `checkFollowingStatus for multiple channels`() = runTest {
        val channels = listOf(
            channel1,
            channel2
        )

        // Mock the isFollowing method in the repository for both channels
        `when`(channelsRepository.isFollowing(channel1)).doSuspendableAnswer { true }
        `when`(channelsRepository.isFollowing(channel2)).doSuspendableAnswer { false }

        // Call the checkFollowingStatus method in the ViewModel
        viewModel.checkFollowingStatus(channels)

        // Advance the scheduler to ensure all coroutines have completed
        testScheduler.advanceUntilIdle()

        // Retrieve the following channels
        val result = viewModel.followingChannels.first()

        // Verify the following status of both channels
        assertTrue(result[channel1.id] == true)
        // if isFollowing returns false, the channel will not be added to the map
        assertTrue(result[channel2.id] == null)
    }

    // Boundary Case Tests
    @Test
    fun `checkFollowingStatus with empty list returns empty results`() = runTest {
        val channels = emptyList<Channel>()

        // Call the checkFollowingStatus method in the ViewModel with an empty list
        viewModel.checkFollowingStatus(channels)

        // Advance the scheduler to ensure all coroutines have completed
        testScheduler.advanceUntilIdle()

        // Retrieve the following channels
        val result = viewModel.followingChannels.first()

        // Verify the results are empty
        assertTrue(result.isEmpty())
    }
}