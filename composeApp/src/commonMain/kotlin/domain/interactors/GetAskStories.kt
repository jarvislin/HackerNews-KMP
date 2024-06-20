package domain.interactors

import domain.models.AskStories
import domain.repositories.ItemRepository

class GetAskStories(private val repository: ItemRepository) {
    suspend operator fun invoke(): List<Long> =
        repository.fetchStories(AskStories)
}
