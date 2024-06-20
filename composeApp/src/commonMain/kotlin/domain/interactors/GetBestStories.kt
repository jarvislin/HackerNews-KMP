package domain.interactors

import domain.models.BestStories
import domain.repositories.ItemRepository

class GetBestStories(private val repository: ItemRepository) {
    suspend operator fun invoke(): List<Long> =
        repository.fetchStories(BestStories)
}
