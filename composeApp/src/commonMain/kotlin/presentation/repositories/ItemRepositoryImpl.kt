package presentation.repositories

import data.remote.models.RawItem
import domain.models.Ask
import domain.models.Category
import domain.models.Comment
import domain.models.Item
import domain.models.Job
import domain.models.Poll
import domain.models.PollOption
import domain.models.Story
import domain.repositories.ItemRepository
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.json.Json

class ItemRepositoryImpl(
    private val client: HttpClient,
    private val json: Json,
) : ItemRepository {
    override suspend fun fetchItems(ids: List<Long>): List<Item> = coroutineScope {
        ids.map { async { fetchItem(it) } }.awaitAll().filterNotNull()
    }

    private suspend fun fetchItem(id: Long): Item? {
        val response = client.get("$ITEM_API_URL/item/$id.json")
        return if (response.status.value in 200..299) {
            val body = response.bodyAsText()
            val item = json.decodeFromString<RawItem>(body)
            when (item.type) {
                TYPE_STORY -> {
                    if (item.url != null) json.decodeFromString<Story>(body)
                    else json.decodeFromString<Ask>(body)
                }

                TYPE_COMMENT -> json.decodeFromString<Comment>(body)
                TYPE_JOB -> json.decodeFromString<Job>(body)
                TYPE_POLL -> json.decodeFromString<Poll>(body)
                TYPE_POLL_OPTION -> json.decodeFromString<PollOption>(body)
                else -> null // ignore unknown types
            }
        } else null // ignore failed request
    }

    override suspend fun fetchStories(category: Category): List<Long> {
        val response = client.get("$ITEM_API_URL/${category.path}")
        return if (response.status.value in 200..299) {
            val body = response.bodyAsText()
            json.decodeFromString<List<Long>>(body)
        } else emptyList()
    }

    companion object {
        private const val ITEM_API_URL = "https://hacker-news.firebaseio.com/v0"
        private const val TYPE_STORY = "story"
        private const val TYPE_COMMENT = "comment"
        private const val TYPE_JOB = "job"
        private const val TYPE_POLL = "poll"
        private const val TYPE_POLL_OPTION = "pollopt"
    }
}