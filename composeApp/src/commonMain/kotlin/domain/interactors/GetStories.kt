package domain.interactors

import domain.models.Category
import domain.repositories.ItemRepository

class GetStories(private val repository: ItemRepository) {
    suspend operator fun invoke(category: Category): List<Long> =
        repository.fetchStories(Category.from(category.index))
}
