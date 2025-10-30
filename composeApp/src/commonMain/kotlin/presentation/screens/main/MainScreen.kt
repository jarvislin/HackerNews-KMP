@file:OptIn(ExperimentalMaterial3Api::class)

package presentation.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import domain.models.Category
import domain.models.Item
import hackernewskmp.composeapp.generated.resources.Res
import hackernewskmp.composeapp.generated.resources.an_error_occurred
import hackernewskmp.composeapp.generated.resources.ic_alt_arrow_down_linear
import hackernewskmp.composeapp.generated.resources.ic_refresh_linear
import hackernewskmp.composeapp.generated.resources.loading
import hackernewskmp.composeapp.generated.resources.retry
import kotlinx.coroutines.flow.mapNotNull
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import presentation.viewmodels.MainViewModel

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
        expandedHeight = 64.dp,
        colors = TopAppBarDefaults.topAppBarColors().run { copy(containerColor = containerColor.copy(alpha = 0.9f)) },
        actions = {
            IconButton(onClick = {  }) {
                Icon(
                    painter = painterResource(Res.drawable.ic_refresh_linear),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        },
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
                Text(
                    text = state.currentCategory.title,
                    style = MaterialTheme.typography.headlineMedium,
                )
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
    contentPadding: PaddingValues = PaddingValues.Zero,
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
                ItemRowWidget(
                    item = item,
                    seen = state.seenItemsIds.contains(item.getItemId().toString()),
                    onClickItem = {
                        viewModel.markItemAsSeen(item)
                        onClickItem(item)
                    },
                    onClickComment = {
                        viewModel.markItemAsSeen(item)
                        onClickComment(item)
                    },
                )
            }

            if (state.items.isNotEmpty() && state.currentPage * MainViewModel.PAGE_SIZE < state.itemIds.size) {
                // only display the loading item if there are items loaded
                item { ItemLoadingWidget() }
            }
        }
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
