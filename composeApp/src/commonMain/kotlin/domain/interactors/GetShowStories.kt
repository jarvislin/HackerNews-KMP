package domain.interactors

import domain.models.ShowStories
import domain.repositories.ItemRepository

class GetShowStories(private val repository: ItemRepository) {
    suspend operator fun invoke(): List<Long> =
        repository.fetchStories(ShowStories)
}
