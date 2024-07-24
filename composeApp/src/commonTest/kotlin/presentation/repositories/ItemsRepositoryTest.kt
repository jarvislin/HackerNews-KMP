package presentation.repositories

import domain.models.TopStories
import domain.repositories.ItemRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ItemsRepositoryTest {

    private lateinit var repository: ItemRepository

    @BeforeTest
    fun setUp(){
        val json = Json { ignoreUnknownKeys = true }
        repository = FakeItemsRepository(json)
    }

    @Test
    fun shouldFetchItems() = runBlocking {
        val ids = listOf<Long>(1, 2, 3)
        val result = repository.fetchItems(ids)
        assertEquals(ids.size, result.size)

        result.forEach {
            assertEquals(true, it.isSuccess)
            assertEquals(false, it.isFailure)
        }
    }

    @Test
    fun shouldFetchStories() = runBlocking {
        val result = repository.fetchStories(TopStories)
        assertEquals(0, result.getOrThrow().size)
        assertEquals(true, result.isSuccess)
        assertEquals(false, result.isFailure)
    }

    @Test
    fun shouldFetchCommentsSuccessfully() = runBlocking {
        val ids = listOf<Long>(1)
        val result = repository.fetchComments(0, ids)
        assertEquals(true, result.first().isSuccess)
        assertEquals(false, result.first().isFailure)
    }

    @Test
    fun shouldHandleFailureWhenGettingComments() = runBlocking {
        val result = repository.fetchComments(0, listOf(0))
        assertEquals(false, result.first().isSuccess)
        assertEquals(true, result.first().isFailure)
    }
}