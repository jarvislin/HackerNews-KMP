package domain.interactors

import domain.models.NewStories
import domain.repositories.ItemRepository

class GetNewStories(private val repository: ItemRepository) {
    suspend operator fun invoke(): List<Long> =
        repository.fetchStories(NewStories)
}
