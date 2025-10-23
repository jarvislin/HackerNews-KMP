@file:OptIn(ExperimentalMaterial3Api::class)

package presentation.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import domain.models.Category
import domain.models.Item
import domain.models.Story
import domain.models.getCommentCount
import domain.models.getFormattedDiffTimeShort
import domain.models.getPoint
import domain.models.getTitle
import domain.models.getUrl
import extensions.trimmedHostName
import hackernewskmp.composeapp.generated.resources.Res
import hackernewskmp.composeapp.generated.resources.an_error_occurred
import hackernewskmp.composeapp.generated.resources.ic_alt_arrow_down_linear
import hackernewskmp.composeapp.generated.resources.ic_chat_line_linear
import hackernewskmp.composeapp.generated.resources.ic_clock_circle_linear
import hackernewskmp.composeapp.generated.resources.ic_like_outline
import hackernewskmp.composeapp.generated.resources.ic_link_minimalistic_linear
import hackernewskmp.composeapp.generated.resources.loading
import hackernewskmp.composeapp.generated.resources.retry
import io.ktor.http.Url
import kotlinx.coroutines.flow.mapNotNull
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject
import presentation.viewmodels.MainViewModel
import ui.Preview
import ui.trimmedTextStyle
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours
import kotlin.time.ExperimentalTime

@Composable
fun MainScreen(onClickItem: (Item) -> Unit, onClickComment: (Item) -> Unit) {
    val viewModel = koinInject<MainViewModel>()
    val state by viewModel.state
    val snackBarHostState = remember { SnackbarHostState() }
    Scaffold(
        topBar = { AppTopBar() },
        snackbarHost = { SnackbarHost(snackBarHostState) },
        content = { padding ->
            val pullToRefreshState = rememberPullToRefreshState()
            PullToRefreshBox(
                isRefreshing = state.refreshing,
                onRefresh = viewModel::onPullToRefresh,
                modifier = Modifier.fillMaxSize(),
                state = pullToRefreshState,
                indicator = {
                    Indicator(
                        modifier = Modifier.padding(padding).align(Alignment.TopCenter),
                        isRefreshing = state.refreshing,
                        state = pullToRefreshState,
                    )
                },
            ) {
                PaginatedItemList(
                    onClickItem = onClickItem,
                    onClickComment = onClickComment,
                    contentPadding = padding,
                )
            }
        }
    )

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

@Composable
fun AppTopBar(viewModel: MainViewModel = koinInject()) {
    val state by viewModel.state
    var expanded by remember { mutableStateOf(false) }

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors().run { copy(containerColor = containerColor.copy(alpha = 0.9f)) },
        title = {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(
                        interactionSource = null,
                        indication = ripple(),
                        onClick = { expanded = true },
                    )
                    .minimumInteractiveComponentSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(state.currentCategory.title)
                Spacer(Modifier.width(8.dp))
                Icon(
                    painter = painterResource(Res.drawable.ic_alt_arrow_down_linear),
                    contentDescription = null,
                )
            }
            DropdownMenu(
                offset = DpOffset(16.dp, 0.dp),
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Category.getAll().forEach { category ->
                    val isCurrent = category == state.currentCategory
                    DropdownMenuItem(
                        modifier = Modifier.minimumInteractiveComponentSize(),
                        text = {
                            Text(
                                text = category.title,
                                style = MaterialTheme.typography.titleMedium,
                                textDecoration = TextDecoration.Underline.takeIf { isCurrent },
                            )
                        },
                        onClick = {
                            viewModel.onClickCategory(category)
                            expanded = false
                        })
                }
            }
        }
    )
}

@Composable
fun PaginatedItemList(
    onClickItem: (Item) -> Unit,
    onClickComment: (Item) -> Unit,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    viewModel: MainViewModel = koinInject()
) {
    val listState = rememberLazyListState()
    val state by viewModel.state

    if (state.itemIds.isEmpty()) {
        viewModel.loadNextPage()
    } else {
        LaunchedEffect(listState) {
            snapshotFlow { listState.layoutInfo.visibleItemsInfo }
                .mapNotNull { visibleItems -> visibleItems.lastOrNull()?.index }
                .collect { lastVisibleItemIndex ->
                    if (lastVisibleItemIndex >= state.items.size - MainViewModel.PAGE_SIZE / 2) {
                        viewModel.loadNextPage()
                    }
                }
        }
    }

    Box {
        if (state.loading && state.items.isEmpty()) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        }
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = contentPadding
        ) {
            itemsIndexed(items = state.items, key = { index, item -> item.getItemId() }) { index, item ->
                ItemRowWidget(item, { onClickItem(item) }, { onClickComment(item) })
            }

            if (state.items.isNotEmpty() && state.currentPage * MainViewModel.PAGE_SIZE < state.itemIds.size) {
                // only display the loading item if there are items loaded
                item { ItemLoadingWidget() }
            }
        }
    }
}

@Composable
fun ItemRowWidget(
    item: Item,
    onClickItem: () -> Unit,
    onClickComment: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
        Column(
            verticalArrangement = spacedBy(8.dp),
            modifier = Modifier
                .weight(1f)
                .clickable(onClick = onClickItem)
                .padding(8.dp)
        ) {
            Text(
                text = item.getTitle(),
                style = MaterialTheme.typography.titleMedium,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = spacedBy(8.dp)
            ) {
                item.getUrl()?.let { urlString ->
                    LabelledIcon(
                        label = Url(urlString).trimmedHostName(),
                        icon = painterResource(Res.drawable.ic_link_minimalistic_linear),
                    )
                }
                LabelledIcon(
                    label = item.getPoint().toString(),
                    icon = painterResource(Res.drawable.ic_like_outline),
                )
                LabelledIcon(
                    label = item.getFormattedDiffTimeShort(),
                    icon = painterResource(Res.drawable.ic_clock_circle_linear),
                )
            }
        }
        item.getCommentCount()?.let { commentCount ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable(onClick = onClickComment)
                    .padding(top = 8.dp, bottom = 8.dp)
                    .minimumInteractiveComponentSize()
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_chat_line_linear),
                    contentDescription = null,
                )
                Text(
                    text = "$commentCount",
                    style = MaterialTheme.typography.labelLarge,
                )

            }
        }

    }
}

@Composable
private fun LabelledIcon(
    label: String,
    icon: Painter? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = spacedBy(4.dp)
    ) {
        if (icon != null) {
            Icon(
                painter = icon,
                contentDescription = null,
                modifier = Modifier
                    .size(16.dp),
            )
        }
        Text(
            text = label,
            fontSize = MaterialTheme.typography.bodySmall.fontSize,
            style = trimmedTextStyle
        )
    }
}

@Composable
fun ItemLoadingWidget() {
    Row(
        modifier = Modifier.fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        CircularProgressIndicator(
            strokeWidth = 2.dp,
            modifier = Modifier.padding(16.dp).size(12.dp)
        )
        Text(
            text = stringResource(Res.string.loading),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
    }
}


@OptIn(ExperimentalTime::class)
@Preview
@Composable
fun Preview_ItemRowWidget() {
    Preview(false) {
        ItemRowWidget(
            item = previewItems.first { it is Story },
            onClickItem = {},
            onClickComment = {}
        )
    }
}

@OptIn(ExperimentalTime::class)
val previewItems: List<Item> = listOf(
    Story(
        id = 12345L,
        title = "Sample Story Title",
        url = "https://www.example.com",
        userName = "sample_user",
        time = (Clock.System.now() - 5.hours).epochSeconds,
        score = 123,
        countOfComment = 45,
        commentIds = listOf(),
    )
)