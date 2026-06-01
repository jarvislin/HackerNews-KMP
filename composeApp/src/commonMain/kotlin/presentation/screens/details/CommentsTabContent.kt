package presentation.screens.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.unit.dp
import domain.models.Comment
import domain.models.Item
import domain.models.PollOption
import domain.models.getCommentIds
import kotlinx.collections.immutable.ImmutableList
import presentation.screens.main.ItemLoadingWidget
import presentation.viewmodels.DetailsState
import utils.Constants

@Composable
fun CommentsTabContent(
    item: Item,
    contentPadding: PaddingValues,
    state: DetailsState,
    pollOptions: ImmutableList<PollOption>,
    onFetchItem: (Item) -> Unit,
    hasComments: () -> Boolean,
    getComment: (Long) -> Comment?,
    isCollapsed: (Long) -> Boolean,
    onToggleCollapse: (Long) -> Unit,
    countDescendants: (Long) -> Int,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(item) {
        onFetchItem(item)
    }

    val localUriHandler = LocalUriHandler.current
    val uriHandler by remember {
        mutableStateOf(object : UriHandler {
            override fun openUri(uri: String) {
                localUriHandler.openUri(decodeUrl(uri))
            }
        })
    }

    CompositionLocalProvider(LocalUriHandler provides uriHandler) {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = contentPadding,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Content
            item(key = "header-${item.getItemId()}") { ItemDetailsSection(item, pollOptions) }

            // Comments
            if (hasComments()) {
                item.getCommentIds().forEach { commentId ->
                    commentItem(
                        commentId = commentId,
                        depth = 0,
                        getComment = getComment,
                        isCollapsed = isCollapsed,
                        onToggleCollapse = onToggleCollapse,
                        countDescendants = countDescendants,
                    )
                }
            }
            else if (state.loadingComments) {
                item(key = "loading-comments") { ItemLoadingWidget() }
            } else {
                //TODO: error state?
            }
        }
    }
}

private fun decodeUrl(url: String): String {
    val entityPattern = Regex(Constants.REGEX_PATTERN)
    return url.replace(entityPattern) { matchResult ->
        val codePoint = matchResult.groupValues[1].toInt(16)
        CharArray(1) { codePoint.toChar() }.concatToString()
    }
}
