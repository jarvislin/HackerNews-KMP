package presentation.repositories

import domain.models.Category
import domain.models.Comment
import domain.models.Item
import domain.repositories.ItemRepository
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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
            return Item.from(json, body)
        } else null // ignore failed request
    }

    override suspend fun fetchStories(category: Category): List<Long> {
        val response = client.get("$ITEM_API_URL/${category.path}")
        return if (response.status.value in 200..299) {
            val body = response.bodyAsText()
            json.decodeFromString<List<Long>>(body)
        } else emptyList()
    }

    override suspend fun fetchComments(depth: Int, ids: List<Long>): Flow<Comment> = flow {
        ids.forEach { id ->
            val comment = fetchItem(id) as? Comment
            if (comment != null) {
                emit(comment.copy(depth = depth))
                if (comment.commentIds.isNotEmpty()) {
                    fetchComments(depth + 1, comment.commentIds).collect { emit(it) }
                }
            }
        }
    }

    companion object {
        private const val ITEM_API_URL = "https://hacker-news.firebaseio.com/v0"
    }
}