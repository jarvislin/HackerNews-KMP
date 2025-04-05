package presentation.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import domain.interactors.GetItems
import domain.interactors.GetStories
import domain.models.Category
import domain.models.Item
import domain.models.TopStories
import kotlinx.coroutines.launch

class MainViewModel(private val getStories: GetStories, private val getItems: GetItems) : ScreenModel {
    private val _state = mutableStateOf(MainState())
    val state: State<MainState> = _state

    fun loadNextPage() {
        if (state.value.loading) return
        if (state.value.error != null) return

        screenModelScope.launch {
            _state.value = state.value.copy(loading = true)
            if (state.value.itemIds.isEmpty()) {
                getStories(state.value.currentCategory)
                    .onSuccess { _state.value = state.value.copy(itemIds = state.value.itemIds + it) }
                    .onFailure { _state.value = state.value.copy(error = it) }
            }
            val nextPageIds = state.value.itemIds.drop(state.value.currentPage * PAGE_SIZE).take(PAGE_SIZE)
            val newItems = getItems(nextPageIds)
            _state.value = state.value.copy(
                loading = false,
                refreshing = false,
                items = state.value.items + newItems,
                currentPage = state.value.currentPage + 1,
            )
        }
    }

    fun reset() {
        _state.value = state.value.copy(
            loading = false, itemIds = emptyList(), items = emptyList(), currentPage = 0, error = null
        )
    }

    fun onPullToRefresh() {
        _state.value = state.value.copy(
            refreshing = true, loading = false, itemIds = emptyList(), items = emptyList(), currentPage = 0, error = null
        )
        loadNextPage()
    }

    fun onClickCategory(item: Category) {
        if (state.value.currentCategory == item) return
        _state.value = state.value.copy(
            currentCategory = item,
            loading = false,
            refreshing = false,
            itemIds = emptyList(),
            items = emptyList(),
            currentPage = 0,
            error = null
        )
        loadNextPage()
    }

    companion object {
        const val PAGE_SIZE = 20
    }
}

data class MainState(
    val items: List<Item> = emptyList(),
    val itemIds: List<Long> = emptyList(),
    val loading: Boolean = false,
    val refreshing: Boolean = false,
    val error: Throwable? = null,
    val currentPage: Int = 0,
    val currentCategory: Category = TopStories,
)