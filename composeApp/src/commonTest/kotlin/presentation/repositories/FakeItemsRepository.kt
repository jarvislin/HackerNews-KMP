package presentation.repositories

import domain.models.Category
import domain.models.Comment
import domain.models.Item
import domain.repositories.ItemRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json

class FakeItemsRepository(
    private val json: Json
) : ItemRepository {

    override suspend fun fetchItems(ids: List<Long>): List<Result<Item?>> = coroutineScope {
        ids.map { async { fetchItem(it) } }.awaitAll()
    }

    private fun fetchItem(id: Long, isComment: Boolean = false): Result<Item?> {
//        val result = if (!isComment) {
//            """
//            {"by":"test","descendants":1,"id":$id,"kids":[1],"score":1,"time":1720481386,"title":"test","type":"story","text":"test","url":"test"}
//            """
//        } else {
//            """
//            {"by":"test", "id":$id, "kids":[1], "parent": 1, "time":1720481386,"title":"test","type":"comment","text":"test"}
//            """
//        }

        val result =
            """
            {"by":"test", "id":$id, "kids":[1], "parent": 1, "time":1720481386,"title":"test","type":"comment","text":"test"}
            """

        return if (id >= 1) {
            Result.success(Item.from(json, result))
        } else {
            Result.failure(Exception("Item not found"))
        }
    }

    override suspend fun fetchStories(category: Category): Result<List<Long>> {
        return Result.success(listOf())
    }

    override suspend fun fetchComments(depth: Int, ids: List<Long>): Flow<Result<Comment>> = flow {
        ids.forEach { id ->
            val result = fetchItem(id, true)
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
}