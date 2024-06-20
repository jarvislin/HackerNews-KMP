package domain.interactors

import domain.models.JobStories
import domain.repositories.ItemRepository

class GetJobStories(private val repository: ItemRepository) {
    suspend operator fun invoke(): List<Long> =
        repository.fetchStories(JobStories)
}
