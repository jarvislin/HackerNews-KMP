package presentation.viewmodels

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
    val itemIds = mutableStateOf(listOf<Long>())
    val items = mutableStateOf(listOf<Item>())
    val currentPage = mutableStateOf(0)
    val currentCategory = mutableStateOf<Category>(TopStories)
    val pageSize = 20
    val isLoading = mutableStateOf(false)
    val error = mutableStateOf<Throwable?>(null)

    fun loadNextPage() {
        if (isLoading.value) return
        if (error.value != null) return

        screenModelScope.launch {
            isLoading.value = true
            if (itemIds.value.isEmpty()) {
                val storyIds = getStories(currentCategory.value)
                if (storyIds.isSuccess) {
                    itemIds.value += storyIds.getOrThrow()
                } else {
                    error.value = storyIds.exceptionOrNull()
                }
            }
            val nextPageIds = itemIds.value.drop(currentPage.value * pageSize).take(pageSize)
            val newItems = getItems(nextPageIds)
            items.value += newItems
            currentPage.value++
            isLoading.value = false
        }
    }

    fun reset() {
        isLoading.value = false
        itemIds.value = emptyList()
        items.value = emptyList()
        currentPage.value = 0
        error.value = null
    }
}