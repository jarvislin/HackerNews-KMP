package presentation.screens.details

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import domain.models.Comment
import domain.models.Item
import domain.models.PollOption
import domain.models.getCommentCount
import domain.models.getUrl
import hackernewskmp.composeapp.generated.resources.Res
import hackernewskmp.composeapp.generated.resources.an_error_occurred
import hackernewskmp.composeapp.generated.resources.retry
import kotlinx.collections.immutable.ImmutableList
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.getString
import org.koin.compose.koinInject
import presentation.viewmodels.DetailsState
import presentation.viewmodels.DetailsViewModel
import presentation.viewmodels.MainViewModel

@Serializable
data class DetailsRoute(
    @SerialName("id")
    val id: Long,
    @SerialName("tab")
    val tab: String // on iOS, NavHost 2.9.1 doesn't like when this is an enum (like DetailsScreenTab)
)

enum class DetailsScreenTab {
    Webview, Comments;

    companion object {
        fun from(value: String) = entries.first { it.name.equals(value, ignoreCase = true) }
    }
}

@Composable
fun DetailsScreen(
    itemId: Long,
    tab: DetailsScreenTab,
    onBack: () -> Unit,
) {
    val detailsViewModel = koinInject<DetailsViewModel>()
    val mainViewModel = koinInject<MainViewModel>()
    val uriHandler = LocalUriHandler.current
    val state by detailsViewModel.state
    val pollOptions by detailsViewModel.pollOptions.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }
    val item = mainViewModel.state.value.items.first { it.getItemId() == itemId }
    val onClickLink = {
        val url = item.getUrl() ?: error("No URL found")
        uriHandler.openUri(url)
    }
    val onShareLink = {
        detailsViewModel.shareLink(item)
    }
    val onShareComments = {
        detailsViewModel.shareComments(item)
    }

    DetailsScreenContent(
        snackBarHostState = snackBarHostState,
        state = state,
        pollOptions = pollOptions,
        item = item,
        tab = tab,
        onBack = onBack,
        onOpenInBrowser = onClickLink,
        onShareLink = onShareLink,
        onShareComments = onShareComments,
        onFetchItem = detailsViewModel::fetchItem,
        hasComments = detailsViewModel::hasComments,
        getComment = detailsViewModel::getComment,
        isCollapsed = detailsViewModel::isCollapsed,
        onToggleCollapse = detailsViewModel::toggleCollapse,
        countDescendants = detailsViewModel::countDescendants,
    )

    LaunchedEffect(Unit) {
        if (state.error != null) {
            val result = snackBarHostState.showSnackbar(
                message = state.error?.message ?: getString(Res.string.an_error_occurred),
                actionLabel = getString(Res.string.retry)
            )
            if (result == SnackbarResult.ActionPerformed) {
                detailsViewModel.reset()
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreenContent(
    snackBarHostState: SnackbarHostState,
    state: DetailsState,
    pollOptions: ImmutableList<PollOption>,
    item: Item,
    tab: DetailsScreenTab,
    onBack: () -> Unit,
    onOpenInBrowser: () -> Unit,
    onShareLink: () -> Unit,
    onShareComments: () -> Unit,
    onFetchItem: (Item) -> Unit,
    hasComments: () -> Boolean,
    getComment: (Long) -> Comment?,
    isCollapsed: (Long) -> Boolean,
    onToggleCollapse: (Long) -> Unit,
    countDescendants: (Long) -> Int,
) {
    val urlString = item.getUrl()
    var selectedTab by remember(tab, urlString) { mutableStateOf(if (urlString == null) DetailsScreenTab.Comments else tab) }
    var isSheetVisible by remember { mutableStateOf(false) }

    DetailsShareSheet(
        isVisible = isSheetVisible,
        item = item,
        onOpenInBrowser = {
            onOpenInBrowser()
            isSheetVisible = false
        },
        onShareLink = {
            onShareLink()
            isSheetVisible = false
        },
        onShareComments = {
            onShareComments()
            isSheetVisible = false
        },
        onVisibility = {
            isSheetVisible = it
        }
    )

    Scaffold(
        topBar = {
            DetailsTopBar(
                selectedTab = selectedTab,
                urlString = urlString,
                commentCount = item.getCommentCount() ?: 0,
                onTabSelected = { selectedTab = it },
                onBack = onBack,
                onClickOpenExternal = { isSheetVisible = true }
            )
        },
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { padding ->
        FadeVisibilityKeepingState(
            visible = selectedTab == DetailsScreenTab.Comments,
        ) {
            CommentsTabContent(
                item = item,
                contentPadding = PaddingValues(
                    top = 8.dp + padding.calculateTopPadding(),
                    start = padding.calculateStartPadding(LocalLayoutDirection.current),
                    end = padding.calculateEndPadding(LocalLayoutDirection.current),
                    bottom = padding.calculateBottomPadding()
                ),
                state = state,
                pollOptions = pollOptions,
                onFetchItem = onFetchItem,
                hasComments = hasComments,
                getComment = getComment,
                isCollapsed = isCollapsed,
                onToggleCollapse = onToggleCollapse,
                countDescendants = countDescendants,
            )
        }
        if (urlString != null) {
            val visible = selectedTab == DetailsScreenTab.Webview
            FadeVisibilityKeepingState(
                visible = visible,
            ) {
                WebviewTabContent(
                    url = urlString,
                    visible = visible,
                    modifier = Modifier
                        .padding(padding)
                )
            }
        }
    }
}

@Composable
fun FadeVisibilityKeepingState(
    visible: Boolean,
    modifier: Modifier = Modifier,
    durationMillis: Int = 300,
    content: @Composable () -> Unit
) {
    val targetAlpha = if (visible) 1f else 0f
    val alpha by animateFloatAsState(targetAlpha, animationSpec = tween(durationMillis))

    Box(
        modifier = modifier
            .alpha(alpha)
            // Drop below others when hidden to let touches pass through
            .zIndex(if (alpha < 0.5f) -1f else 0f)
    ) {
        content()
    }
}
