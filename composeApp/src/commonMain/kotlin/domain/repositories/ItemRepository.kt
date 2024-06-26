package domain.repositories

import domain.models.Category
import domain.models.Comment
import domain.models.Item
import kotlinx.coroutines.flow.Flow

interface ItemRepository {
    suspend fun fetchItems(ids: List<Long>): List<Item>
    suspend fun fetchStories(category: Category): Result<List<Long>>
    suspend fun fetchComments(depth:Int, ids: List<Long>): Flow<Result<Comment>>
}