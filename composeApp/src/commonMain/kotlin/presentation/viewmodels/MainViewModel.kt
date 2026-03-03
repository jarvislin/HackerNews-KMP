package presentation.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.local.AppPreferences
import domain.interactors.GetItems
import domain.interactors.GetStories
import domain.models.Category
import domain.models.Item
import domain.models.TopStories
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentSet
import kotlinx.coroutines.launch

class MainViewModel(
    private val getStories: GetStories,
    private val getItems: GetItems,
    private val appPreferences: AppPreferences,
) : ViewModel() {
    private val _state = mutableStateOf(MainState())
    val state: State<MainState> = _state

    init {
        viewModelScope.launch {
            appPreferences.seenItemList.collect {
                _state.value = state.value.copy(seenItemsIds = it.toPersistentSet())
            }
        }
    }

    fun loadNextPage() {
        if (state.value.loading) return
        if (state.value.error != null) return

        viewModelScope.launch {
            _state.value = state.value.copy(loading = true)
            if (state.value.itemIds.isEmpty()) {
                getStories(state.value.currentCategory)
                    .onSuccess { _state.value = state.value.copy(itemIds = (state.value.itemIds + it).toPersistentList()) }
                    .onFailure { _state.value = state.value.copy(error = it) }
            }
            val nextPageIds = state.value.itemIds.drop(state.value.currentPage * PAGE_SIZE).take(PAGE_SIZE)
            val newItems = getItems(nextPageIds)
            _state.value = state.value.copy(
                loading = false,
                refreshing = false,
                items = (state.value.items + newItems).toPersistentList(),
                currentPage = state.value.currentPage + 1,
            )
        }
    }

    fun reset() {
        _state.value = state.value.copy(
            loading = false, itemIds = persistentListOf(), items = persistentListOf(), currentPage = 0, error = null
        )
    }

    fun onPullToRefresh() {
        _state.value = state.value.copy(
            refreshing = true, loading = false, itemIds = persistentListOf(), items = persistentListOf(), currentPage = 0, error = null
        )
        loadNextPage()
    }

    fun onClickCategory(item: Category) {
        if (state.value.currentCategory == item) return
        _state.value = state.value.copy(
            currentCategory = item,
            loading = false,
            refreshing = false,
            itemIds = persistentListOf(),
            items = persistentListOf(),
            currentPage = 0,
            error = null
        )
        loadNextPage()
    }

    fun markItemAsSeen(item: Item) {
        viewModelScope.launch {
            appPreferences.markItemAsSeen(item.getItemId().toString())
        }
    }

    companion object {
        const val PAGE_SIZE = 20
    }
}

data class MainState(
    val items: ImmutableList<Item> = persistentListOf(),
    val itemIds: ImmutableList<Long> = persistentListOf(),
    val seenItemsIds: ImmutableSet<String> = persistentSetOf(),
    val loading: Boolean = false,
    val refreshing: Boolean = false,
    val error: Throwable? = null,
    val currentPage: Int = 0,
    val currentCategory: Category = TopStories,
)