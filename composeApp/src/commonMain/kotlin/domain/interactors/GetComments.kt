package domain.interactors

import domain.models.Comment
import domain.repositories.ItemRepository
import kotlinx.coroutines.flow.Flow

class GetComments(private val repository: ItemRepository) {
    suspend operator fun invoke(ids: List<Long>): Flow<Result<Comment>> =
        repository.fetchComments(0, ids)
}