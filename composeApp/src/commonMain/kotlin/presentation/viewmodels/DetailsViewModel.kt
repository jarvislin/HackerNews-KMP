package presentation.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.interactors.GetComments
import domain.interactors.GetPollOptions
import domain.models.Comment
import domain.models.PollOption
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch

class DetailsViewModel(
    private val getComments: GetComments,
    private val getPollOptions: GetPollOptions
) : ViewModel() {
    private val _state = mutableStateOf(DetailsState())
    val state: State<DetailsState> = _state

    fun loadComments(ids: List<Long>) {
        viewModelScope.launch {
            _state.value = state.value.copy(loadingComments = true)
            getComments(ids).takeWhile { result ->
                val shouldContinue = result.isSuccess
                if (shouldContinue.not()) {
                    _state.value = state.value.copy(
                        loadingComments = false,
                        error = result.exceptionOrNull()
                    )
                }
                shouldContinue
            }.collect { result ->
                _state.value = state.value.copy(comments = state.value.comments + result.getOrThrow())
            }
        }
    }

    fun loadPollOptions(optionIds: List<Long>) {
        viewModelScope.launch {
            _state.value = state.value.copy(loadingPollOptions = true)
            getPollOptions(optionIds).fold(
                onSuccess = {
                    _state.value = state.value.copy(
                        pollOptions = it,
                        loadingPollOptions = false
                    )
                },
                onFailure = {
                    _state.value = state.value.copy(
                        error = it,
                        loadingPollOptions = false
                    )
                }
            )
        }
    }

    fun reset() {
        _state.value = DetailsState()
    }
}

data class DetailsState(
    val pollOptions: List<PollOption> = emptyList(),
    val comments: List<Comment> = emptyList(),
    val loadingPollOptions: Boolean = false,
    val loadingComments: Boolean = false,
    val error: Throwable? = null
)