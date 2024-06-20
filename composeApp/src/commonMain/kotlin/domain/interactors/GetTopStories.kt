package domain.interactors

import domain.models.TopStories
import domain.repositories.ItemRepository

class GetTopStories(private val repository: ItemRepository) {
    suspend operator fun invoke(): List<Long> =
        repository.fetchStories(TopStories)
}
