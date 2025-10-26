@file:OptIn(ExperimentalMaterial3Api::class)

package presentation.screens.details

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.multiplatform.webview.web.rememberWebViewNavigator
import domain.models.Item
import domain.models.getCommentCount
import domain.models.getUrl
import extensions.trimmedHostName
import getPlatform
import domain.models.getUserName
import hackernewskmp.composeapp.generated.resources.Res
import hackernewskmp.composeapp.generated.resources.an_error_occurred
import hackernewskmp.composeapp.generated.resources.back
import hackernewskmp.composeapp.generated.resources.ic_arrow_left_linear
import hackernewskmp.composeapp.generated.resources.ic_chat_line_linear
import hackernewskmp.composeapp.generated.resources.ic_link_minimalistic_linear
import hackernewskmp.composeapp.generated.resources.ic_square_top_down_linear
import hackernewskmp.composeapp.generated.resources.retry
import hackernewskmp.composeapp.generated.resources.x_comments
import io.ktor.http.Url
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject
import presentation.viewmodels.DetailsViewModel
import presentation.viewmodels.MainViewModel
import presentation.widgets.PlatformSwipeContainer
import ui.AppPreview
import ui.trimmedTextStyle
import utils.Constants
import kotlin.time.ExperimentalTime

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
    val viewModel = koinInject<DetailsViewModel>()
    val mainViewModel = koinInject<MainViewModel>()
    val uriHandler = LocalUriHandler.current
    val state by viewModel.state
    val snackBarHostState = remember { SnackbarHostState() }
    val item = mainViewModel.state.value.items.first { it.getItemId() == itemId }
    val onClickLink = item.getUrl()?.let {
        { uriHandler.openUri(it) }
    }

    DetailsScreenContent(snackBarHostState, viewModel, item, onBack, onClickLink)

    LaunchedEffect(Unit) {
        if (state.error != null) {
            val result = snackBarHostState.showSnackbar(
                message = state.error?.message ?: getString(Res.string.an_error_occurred),
                actionLabel = getString(Res.string.retry)
            )
            if (result == SnackbarResult.ActionPerformed) {
                viewModel.reset()
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
    BackHandler { onBack() }

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

@OptIn(ExperimentalTime::class)
fun String.toUrl(): Url? = runCatching { Url(this) }.getOrNull()

@Composable
fun DetailsTopBar(
    selectedTabIndex: Int,
    urlString: String?,
    commentCount: Int,
    onTabSelected: (Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    onClickLink: (() -> Unit)? = null
) {
    val trimmedHostName = urlString?.toUrl()?.trimmedHostName()
    val commentsLabel = pluralStringResource(Res.plurals.x_comments, commentCount, commentCount)
    CenterAlignedTopAppBar(
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors().run { copy(containerColor = containerColor.copy(alpha = 0.9f)) },
        title = {
            if (urlString != null) {
                PrimaryTabRow(
                    containerColor = Color.Transparent,
                    selectedTabIndex = selectedTabIndex,
                    divider = {},
                    indicator = {
                        TabRowDefaults.PrimaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(selectedTabIndex, matchContentSize = true),
                            width = Dp.Unspecified,
                        )
                    },
                    tabs = {
                        DetailsTab(
                            selected = selectedTabIndex == 0,
                            label = commentsLabel,
                            onclick = { onTabSelected(0) },
                            icon = painterResource(Res.drawable.ic_chat_line_linear)
                        )
                        if (trimmedHostName != null) {
                            DetailsTab(
                                selected = selectedTabIndex == 1,
                                label = trimmedHostName,
                                onclick = { onTabSelected(1) },
                                icon = painterResource(Res.drawable.ic_link_minimalistic_linear)
                            )
                        }
                    }
                )
            }
            else {
                Text(commentsLabel)
            }
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    painter = painterResource(Res.drawable.ic_arrow_left_linear),
                    contentDescription = stringResource(Res.string.back),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        actions = {
            if (urlString != null && onClickLink != null) {
                IconButton(onClick = onClickLink) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_square_top_down_linear),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    )
}

@Composable
private fun DetailsTab(
    selected: Boolean,
    label: String,
    onclick: () -> Unit,
    icon: Painter,
    modifier: Modifier = Modifier,
) {
    Tab(
        modifier = modifier,
        selected = selected,
        onClick = onclick,
        icon = {
            Icon(
                painter = icon,
                contentDescription = null,
            )
        },
        text = {
            Text(
                text = label,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    )
}

@Preview(widthDp = 432)
@Composable
fun Preview_DetailsTopBar() {
    AppPreview {
        DetailsTopBar(
            selectedTabIndex = 0,
            urlString = "https://www.example.com",
            commentCount = 10,
            onTabSelected = {},
            onBack = {},
            onClickLink = {},
            modifier = Modifier.border(1.dp, Color.Black)
        )
    }
}