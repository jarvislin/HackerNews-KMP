package presentation.viewmodels

import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import domain.interactors.GetComments
import domain.interactors.GetPollOptions
import domain.models.Comment
import domain.models.PollOption
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch

class DetailsViewModel(
    private val getComments: GetComments,
    private val getPollOptions: GetPollOptions
) : ScreenModel {
    val pollOptions = mutableStateOf(listOf<PollOption>())
    val comments = mutableStateOf(listOf<Comment>())
    val isLoadingPollOptions = mutableStateOf(false)
    val isLoadingComments = mutableStateOf(false)
    val error = mutableStateOf<Throwable?>(null)

    fun loadComments(ids: List<Long>) {
        screenModelScope.launch {
            isLoadingComments.value = true
            getComments(ids).takeWhile { result ->
                val shouldContinue = result.isSuccess
                if (shouldContinue.not()) {
                    error.value = result.exceptionOrNull()
                    isLoadingComments.value = false
                }
                shouldContinue
            }.collect { result ->
                comments.value += result.getOrThrow()
            }
        }
    }

    fun loadPollOptions(optionIds: List<Long>) {
        screenModelScope.launch {
            isLoadingPollOptions.value = true
            getPollOptions(optionIds).fold(
                onSuccess = {
                    pollOptions.value = it
                    isLoadingPollOptions.value = false
                },
                onFailure = {
                    error.value = it
                    isLoadingPollOptions.value = false
                }
            )
        }
    }

    fun reset() {
        pollOptions.value = emptyList()
        comments.value = emptyList()
        isLoadingPollOptions.value = false
        isLoadingComments.value = false
        error.value = null
    }
}