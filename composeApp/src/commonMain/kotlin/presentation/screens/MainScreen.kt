@file:OptIn(ExperimentalMaterial3Api::class)

package presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import domain.models.Category
import domain.models.Item
import domain.models.getCommentCount
import domain.models.getFormattedDiffTime
import domain.models.getPoint
import domain.models.getTitle
import domain.models.getUrl
import extensions.trimmedHostName
import hackernewskmp.composeapp.generated.resources.Res
import hackernewskmp.composeapp.generated.resources.an_error_occurred
import hackernewskmp.composeapp.generated.resources.ic_alt_arrow_down_linear
import hackernewskmp.composeapp.generated.resources.ic_chat_line_linear
import hackernewskmp.composeapp.generated.resources.ic_clock_circle_linear
import hackernewskmp.composeapp.generated.resources.ic_link_minimalistic_linear
import hackernewskmp.composeapp.generated.resources.loading
import hackernewskmp.composeapp.generated.resources.points
import hackernewskmp.composeapp.generated.resources.retry
import io.ktor.http.Url
import kotlinx.coroutines.flow.mapNotNull
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import presentation.viewmodels.MainViewModel
import ui.trimmedTextStyle

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
    onClickComment: () -> Unit
) {
    Column(
        Modifier.fillMaxWidth().clickable(onClick = onClickItem)
    ) {
        Spacer(Modifier.height(12.dp))
        Text(
            item.getTitle(),
            Modifier.padding(horizontal = 16.dp),
            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
            fontFamily = MaterialTheme.typography.headlineSmall.fontFamily
        )
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            item.getUrl()?.let { urlString ->
                Icon(
                    painter = painterResource(Res.drawable.ic_link_minimalistic_linear),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .size(16.dp),
                )
                Text(
                    Url(urlString).trimmedHostName(),
                    Modifier.padding(horizontal = 4.dp),
                    fontSize = MaterialTheme.typography.bodySmall.fontSize,
                    style = trimmedTextStyle
                )
            }
            Icon(
                painter = painterResource(Res.drawable.ic_clock_circle_linear),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .size(16.dp)
            )
            Text(
                item.getFormattedDiffTime(),
                fontSize = MaterialTheme.typography.bodySmall.fontSize,
                modifier = Modifier.padding(start = 4.dp),
                style = trimmedTextStyle
            )
        }
        Spacer(Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Card {
                Text(
                    text = stringResource(Res.string.points, item.getPoint()),
                    fontSize = MaterialTheme.typography.bodySmall.fontSize,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = trimmedTextStyle
                )
            }
            item.getCommentCount()?.let {
                CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
                    Card(
                        modifier = Modifier.padding(start = 8.dp),
                        onClick = onClickComment
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_chat_line_linear),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier
                                    .size(16.dp)
                            )
                            Text(
                                text = item.getCommentCount().toString(),
                                fontSize = MaterialTheme.typography.bodySmall.fontSize,
                                modifier = Modifier.padding(start = 4.dp),
                                style = trimmedTextStyle
                            )
                        }
                    }
                }
            }
        }
        Spacer(Modifier.size(12.dp))
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
