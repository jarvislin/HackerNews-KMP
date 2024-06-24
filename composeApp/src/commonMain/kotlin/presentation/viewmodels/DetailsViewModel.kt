package presentation.viewmodels

import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import domain.interactors.GetComments
import domain.models.Comment
import kotlinx.coroutines.launch

class DetailsViewModel(private val getComments: GetComments) : ScreenModel {
    val comments = mutableStateOf(listOf<Comment>())
    val isLoading = mutableStateOf(false)

    fun loadComments(ids: List<Long>) {
        screenModelScope.launch {
            isLoading.value = true
            getComments(ids).collect {
                comments.value += it
            }
        }
    }
}