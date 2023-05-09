package com.dicoding.storyapp.ui.liststory

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.dicoding.storyapp.response.ListStoryItem
import com.dicoding.storyapp.tools.Repository
import com.dicoding.storyapp.utils.DataDummy
import com.dicoding.storyapp.utils.MainDispatcherRule
import com.dicoding.storyapp.utils.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class ListStoryViewModelTest {

    @Mock
    private lateinit var repository: Repository
    private lateinit var listStoryViewModel: ListStoryViewModel
    private val dummyStories = DataDummy.generateDummyStory()
    private val dummyToken = "ini dummy token"

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setUp() {
        listStoryViewModel = ListStoryViewModel(repository)
    }

    @Test
    fun `when Get StoryList Should Not Null and Return Success`() = runTest{
        val expectedStories = MutableLiveData<PagingData<ListStoryItem>>()
        expectedStories.value = PagingData.from(dummyStories)

        Mockito.`when`(repository.getStories(dummyToken)).thenReturn(expectedStories)

        val actualResult = listStoryViewModel.getStoriesList(dummyToken).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = ListStoryAdapter.DIFF_CALLBACK_ITEM,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualResult)
        Assert.assertEquals(dummyStories, differ.snapshot())
        Assert.assertEquals(dummyStories.size, differ.snapshot().size)
        Assert.assertEquals(dummyStories[0], differ.snapshot()[0])
    }

    @Test
    fun `when Get StoryList Empty Should Return No Data`() = runTest {
        val data: PagingData<ListStoryItem> = PagingData.from(emptyList())
        val expectedStories = MutableLiveData<PagingData<ListStoryItem>>()
        expectedStories.value = data
        Mockito.`when`(repository.getStories(dummyToken)).thenReturn(expectedStories)
        val actualResult: PagingData<ListStoryItem> = listStoryViewModel.getStoriesList(dummyToken).getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = ListStoryAdapter.DIFF_CALLBACK_ITEM,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualResult)
        Assert.assertEquals(0, differ.snapshot().size)
    }

    private val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }
}
