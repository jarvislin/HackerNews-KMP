package domain.interactors

import domain.models.Item
import domain.repositories.ItemRepository

/**
 * Use case to get items.
 */
class GetItems(private val repository: ItemRepository) {
    suspend operator fun invoke(ids:List<Long>): List<Item> {
        return repository.fetchItems(ids)
    }
}