package domain.interactors

import domain.models.PollOption
import domain.models.UnknownError
import domain.models.getPoint
import domain.repositories.ItemRepository

class GetPollOptions(private val repository: ItemRepository) {
    suspend operator fun invoke(ids: List<Long>): Result<List<PollOption>> =
        repository.fetchItems(ids)
            .mapNotNull { it.getOrNull() }
            .map { it as PollOption }
            .sortedByDescending { it.getPoint() }
            .let {
                if (it.size == ids.size) Result.success(it)
                else Result.failure(UnknownError)
            }
}