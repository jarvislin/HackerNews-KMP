package presentation.screens.details

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import domain.models.Item
import domain.models.getCommentCount
import domain.models.getUrl
import hackernewskmp.composeapp.generated.resources.Res
import hackernewskmp.composeapp.generated.resources.an_error_occurred
import hackernewskmp.composeapp.generated.resources.retry
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.getString
import org.koin.compose.koinInject
import presentation.viewmodels.DetailsViewModel
import presentation.viewmodels.MainViewModel

@Serializable
data class DetailsRoute(
    @SerialName("id")
    val id: Long
)

@Composable
fun DetailsScreen(
    itemId: Long,
    onBack: () -> Unit,
) {
    val detailsViewModel = koinInject<DetailsViewModel>()
    val mainViewModel = koinInject<MainViewModel>()
    val uriHandler = LocalUriHandler.current
    val state by detailsViewModel.state
    val snackBarHostState = remember { SnackbarHostState() }
    val item = mainViewModel.state.value.items.first { it.getItemId() == itemId }
    val onClickLink = item.getUrl()?.let {
        { uriHandler.openUri(it) }
    }

    DetailsScreenContent(snackBarHostState, detailsViewModel, item, onBack, onClickLink)


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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DetailsScreenContent(
    snackBarHostState: SnackbarHostState,
    viewModel: DetailsViewModel,
    item: Item,
    onBack: () -> Unit,
    onClickLink: (() -> Unit)?
) {
    val urlString = item.getUrl()
    var selectedTabIndex by remember { mutableStateOf(if (urlString != null) 1 else 0) }

    Scaffold(
        topBar = {
            DetailsTopBar(
                selectedTabIndex = selectedTabIndex,
                urlString = urlString,
                commentCount = item.getCommentCount() ?: 0,
                onTabSelected = { selectedTabIndex = it },
                onBack = onBack,
                onClickLink = onClickLink.takeIf { urlString != null }
            )
        },
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { padding ->
        FadeVisibilityKeepingState(
            visible = selectedTabIndex == 0,
        ) {
            CommentsTabContent(
                item = item,
                contentPadding = PaddingValues(
                    top = 8.dp + padding.calculateTopPadding(),
                    start = padding.calculateStartPadding(LocalLayoutDirection.current),
                    end = padding.calculateEndPadding(LocalLayoutDirection.current),
                    bottom = padding.calculateBottomPadding()
                ),
                viewModel = viewModel,
            )
        }
        if (urlString != null) {
            FadeVisibilityKeepingState(
                visible = selectedTabIndex == 1,
            ) {
                WebviewTabContent(
                    url = urlString,
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
