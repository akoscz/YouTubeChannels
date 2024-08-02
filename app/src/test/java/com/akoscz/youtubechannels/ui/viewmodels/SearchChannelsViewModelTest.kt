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

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testSearchChannels(): Unit = runTest {
        val query = "test"
        val mockChannels = listOf(
            Channel(id = "1",
                title = "Channel 1",
                description = "Description 1",
                thumbnailDefaultUrl = "",
                thumbnailHighUrl = "",
                thumbnailMediumUrl = "",
                channelDetailsId = "1"
            )
        )

        // Create a mock PagingSource
        val mockPagingSource = object : PagingSource<Int, Channel>() {
            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Channel> {
                return LoadResult.Page(
                    data = mockChannels,
                    prevKey = null,
                    nextKey = null
                )
            }

            override fun getRefreshKey(state: PagingState<Int, Channel>): Int? {
                return null
            }
        }

        // Mock the Pager to use the mock PagingSource
        val mockPager = Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = { mockPagingSource }
        )

        // Inject the mock Pager into the ViewModel
        val viewModel = SearchChannelsViewModel(channelsRepository)
        viewModel.setPager(mockPager)

        // Call the searchChannels method in the ViewModel
        viewModel.searchChannels(query)
        val result = viewModel.searchResults.asSnapshot()

        // Verify the results
        result.map { channel ->
            println(channel)
            assertEquals(mockChannels[0], channel)
        }
    }

    @Test
    fun testUpdateSearchQuery() = runTest {
        val newQuery = "new query"
        val viewModel = SearchChannelsViewModel(channelsRepository)
        viewModel.updateSearchQuery(newQuery)
        val result = viewModel.searchQuery.first()
        assertEquals(newQuery, result)
    }

    @Test
    fun testFollowChannel() = runTest {
        val channel = Channel(
            id = "1",
            title = "Channel 1",
            description = "Description 1",
            thumbnailDefaultUrl = "",
            thumbnailHighUrl = "",
            thumbnailMediumUrl = "",
            channelDetailsId = "1"
        )
        `when`(channelsRepository.followChannel(channel)).doSuspendableAnswer {
            // Simulate the followChannel behavior
            Unit
        }
        val viewModel = SearchChannelsViewModel(channelsRepository)
        viewModel.followChannel(channel)
        // Advance the scheduler to ensure all coroutines have completed
        testScheduler.advanceUntilIdle()

        val result = viewModel.followingChannels.first()
        println("result: $result")

        assertTrue(result[channel.id] == true)
    }

    @Test
    fun testCheckFollowingStatus() = runTest {
        val channel = Channel(
            id = "1",
            title = "Channel 1",
            description = "Description 1",
            thumbnailDefaultUrl = "",
            thumbnailHighUrl = "",
            thumbnailMediumUrl = "",
            channelDetailsId = "1"
        )
        val channels = listOf(channel)

        `when`(channelsRepository.isFollowing(channel)).doSuspendableAnswer {
            // Simulate the isFollowing behavior
            true
        }

        val viewModel = SearchChannelsViewModel(channelsRepository)
        viewModel.checkFollowingStatus(channels)
        // Advance the scheduler to ensure all coroutines have completed
        testScheduler.advanceUntilIdle()

        val result = viewModel.followingChannels.first()
        println("result: $result")
        assertTrue(result[channel.id] == true)
    }
}