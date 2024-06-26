package presentation.repositories

import data.remote.ApiHandler
import domain.models.Category
import domain.models.Comment
import domain.models.Item
import domain.repositories.ItemRepository
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

class ItemRepositoryImpl(
    private val client: HttpClient,
    private val json: Json,
    private val apiHandler: ApiHandler
) : ItemRepository {
    override suspend fun fetchItems(ids: List<Long>): List<Item> = coroutineScope {
        // warn: it might cause issues if the item is not found
        ids.map { async { fetchItem(it) } }.awaitAll().mapNotNull { it.getOrNull() }
    }

    private suspend fun fetchItem(id: Long): Result<Item?> {
        val result = apiHandler.run { client.get("$API_URL/item/$id.json") }
        return if (result.isSuccess) {
            Result.success(Item.from(json, result.getOrThrow()))
        } else {
            Result.failure(result.exceptionOrNull()!!)
        }
    }

    override suspend fun fetchStories(category: Category): Result<List<Long>> =
        apiHandler.runAndParse(json, ListSerializer(Long.serializer())) {
            client.get("$API_URL/${category.path}")
        }

    override suspend fun fetchComments(depth: Int, ids: List<Long>): Flow<Result<Comment>> = flow {
        ids.forEach { id ->
            val result = fetchItem(id)
            if (result.isSuccess) {
                val comment = result.getOrThrow() as? Comment
                if (comment != null) {
                    emit(Result.success(comment.copy(depth = depth)))
                    if (comment.commentIds.isNotEmpty()) {
                        fetchComments(depth + 1, comment.commentIds).collect { emit(it) }
                    }
                }
            } else {
                emit(Result.failure(result.exceptionOrNull()!!))
            }
        }
    }

    companion object {
        private const val API_URL = "https://hacker-news.firebaseio.com/v0"
    }
}
