package domain.repositories

import domain.models.Item

interface ItemRepository {
    suspend fun fetchItems(ids: List<Long>): List<Item>
}