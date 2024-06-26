package presentation.viewmodels

import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import domain.interactors.GetComments
import domain.models.Comment
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch

class DetailsViewModel(private val getComments: GetComments) : ScreenModel {
    val comments = mutableStateOf(listOf<Comment>())
    val isLoading = mutableStateOf(false)
    val error = mutableStateOf<Throwable?>(null)

    fun loadComments(ids: List<Long>) {
        screenModelScope.launch {
            isLoading.value = true
            getComments(ids).takeWhile { result ->
                val shouldContinue = result.isSuccess
                if (shouldContinue.not()) {
                    error.value = result.exceptionOrNull()
                    isLoading.value = false
                }
                shouldContinue
            }.collect { result ->
                comments.value += result.getOrThrow()
            }
        }
    }

    fun reset() {
        comments.value = emptyList()
        isLoading.value = false
        error.value = null
    }
}