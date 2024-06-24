package domain.interactors

import domain.repositories.ItemRepository

class GetComments(private val repository: ItemRepository) {
    suspend operator fun invoke(ids: List<Long>) = repository.fetchComments(0, ids)
}