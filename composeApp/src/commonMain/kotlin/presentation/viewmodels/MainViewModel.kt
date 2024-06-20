package presentation.viewmodels

import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import domain.interactors.GetBestStories
import domain.interactors.GetItems
import domain.models.Item
import kotlinx.coroutines.launch

class MainViewModel(private val getBestStories: GetBestStories, private val getItems: GetItems) : ScreenModel {
    val itemIds = mutableStateOf(listOf<Long>())
    val items = mutableStateOf(listOf<Item>())
    val currentPage = mutableStateOf(0)
    val pageSize = 20

    fun loadNextPage() {
        screenModelScope.launch {
            if (itemIds.value.isEmpty()) {
                itemIds.value += getBestStories()
            }
            val nextPageIds = itemIds.value.drop(currentPage.value * pageSize).take(pageSize)
            val newItems = getItems(nextPageIds)
            items.value += newItems
            currentPage.value++
        }
    }
}