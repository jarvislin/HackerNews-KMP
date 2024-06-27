package domain.interactors

import domain.models.Category
import domain.repositories.ItemRepository

/**
 * Use case to get story ids
 */
class GetStories(private val repository: ItemRepository) {
    suspend operator fun invoke(category: Category): Result<List<Long>> =
        repository.fetchStories(Category.from(category.index))
}
