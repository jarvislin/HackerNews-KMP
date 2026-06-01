package presentation.viewmodels

import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.interactors.GetComments
import domain.interactors.GetPollOptions
import domain.models.Comment
import domain.models.Item
import domain.models.Poll
import domain.models.PollOption
import domain.models.getCommentIds
import domain.models.getTitle
import extensions.shareCommentsText
import extensions.shareLinkText
import getPlatform
import io.github.aakira.napier.Napier
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch

class DetailsViewModel(
    private val getComments: GetComments,
    private val getPollOptions: GetPollOptions
) : ViewModel() {
    private val _state = mutableStateOf(DetailsState())
    val state: State<DetailsState> = _state

    private val _comments = mutableStateMapOf<Long, Comment>()

    private val _pollOptions = MutableStateFlow<ImmutableList<PollOption>>(persistentListOf())
    val pollOptions = _pollOptions.asStateFlow()

    private val _collapsedStates = mutableStateMapOf<Long, Boolean>()

    fun fetchItem(item: Item) {
        if (item is Poll) loadPollOptions(item.optionIds)
        loadComments(item.getCommentIds())
    }
    fun loadComments(ids: List<Long>) {
        viewModelScope.launch {
            _state.value = state.value.copy(loadingComments = true)
            getComments(ids)
                .takeWhile { result ->
                    val shouldContinue = result.isSuccess
                    if (shouldContinue.not()) {
                        _state.value = state.value.copy(
                            loadingComments = false,
                            error = result.exceptionOrNull()
                        )
                    }
                    shouldContinue
                }
                .collect { result ->
                    result.getOrNull()?.let { _comments[it.id] = it }
                }
            _state.value = state.value.copy(loadingComments = false)
        }
    }

    fun hasComments() = _comments.isNotEmpty()

    fun loadPollOptions(optionIds: List<Long>) {
        viewModelScope.launch {
            _state.value = state.value.copy(loadingPollOptions = true)
            getPollOptions(optionIds).fold(
                onSuccess = {
                    _pollOptions.value = it.toPersistentList()
                    _state.value = state.value.copy(
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

    fun getComment(id: Long): Comment? = _comments[id]

    fun isCollapsed(commentId: Long): Boolean = _collapsedStates[commentId] ?: false

    fun toggleCollapse(commentId: Long) {
        if (_comments[commentId]?.commentIds?.isNotEmpty() == true)
            _collapsedStates[commentId] = !(_collapsedStates[commentId] ?: false)
    }

    fun countDescendants(commentId: Long): Int {
        val sum = _comments[commentId]
            ?.run { commentIds.size + commentIds.sumOf(::countDescendants) }
            ?: 0
        return sum
    }

    fun reset() {
        _state.value = DetailsState()
    }

    fun shareLink(item: Item) {
        val shareTitle = item.getTitle()
        val shareText = item.shareLinkText()
        getPlatform().share(shareTitle, shareText)
    }

    fun shareComments(item: Item) {
        val shareTitle = item.getTitle()
        val shareText = item.shareCommentsText()
        getPlatform().share(shareTitle, shareText)
    }
}

@Stable
data class DetailsState(
    val loadingPollOptions: Boolean = false,
    val loadingComments: Boolean = false,
    val error: Throwable? = null
)
