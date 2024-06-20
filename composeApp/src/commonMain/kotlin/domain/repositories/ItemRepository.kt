package domain.repositories

import domain.models.Category
import domain.models.Item

interface ItemRepository {
    suspend fun fetchItems(ids: List<Long>): List<Item>
    suspend fun fetchStories(category: Category): List<Long>
}