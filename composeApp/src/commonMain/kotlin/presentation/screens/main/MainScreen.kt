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
import androidx.compose.foundation.lazy.items
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
import hackernewskmp.composeapp.generated.resources.about
import hackernewskmp.composeapp.generated.resources.an_error_occurred
import hackernewskmp.composeapp.generated.resources.ic_alt_arrow_down_linear
import hackernewskmp.composeapp.generated.resources.ic_info_circle_linear
import hackernewskmp.composeapp.generated.resources.loading
import hackernewskmp.composeapp.generated.resources.retry
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import presentation.viewmodels.MainViewModel

@Serializable
object MainRoute

@Composable
fun MainScreen(
    onClickItem: (Item) -> Unit,
    onClickComment: (Item) -> Unit,
    onClickAbout: () -> Unit,
) {
    val viewModel = koinInject<MainViewModel>()
    val state by viewModel.state
    val isRefreshIndicatorVisible = state.refreshing || (state.loading && state.items.isEmpty())
    val snackBarHostState = remember { SnackbarHostState() }
    Scaffold(
        topBar = {
            AppTopBar(
                currentCategory = viewModel.state.value.currentCategory,
                onClickCategory = viewModel::onClickCategory,
                onClickAbout = onClickAbout,
            )
         },
        snackbarHost = { SnackbarHost(snackBarHostState) },
        content = { padding ->
            val pullToRefreshState = rememberPullToRefreshState()
            PullToRefreshBox(
                isRefreshing = isRefreshIndicatorVisible,
                onRefresh = viewModel::onPullToRefresh,
                modifier = Modifier.fillMaxSize(),
                state = pullToRefreshState,
                indicator = {
                    Indicator(
                        modifier = Modifier.padding(padding).align(Alignment.TopCenter),
                        isRefreshing = isRefreshIndicatorVisible,
                        state = pullToRefreshState,
                    )
                },
            ) {
                PaginatedItemList(
                    loading = state.loading,
                    currentPage = state.currentPage,
                    itemIds = state.itemIds,
                    items = state.items,
                    seenItemsIds = state.seenItemsIds,
                    onClickItem = onClickItem,
                    onClickComment = onClickComment,
                    onLoadNextPage = viewModel::loadNextPage,
                    onMarkItemAsSeen = viewModel::markItemAsSeen,
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
fun AppTopBar(
    currentCategory: Category,
    onClickAbout: () -> Unit,
    onClickCategory: (Category) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    TopAppBar(
        expandedHeight = 64.dp,
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
                Text(
                    text = currentCategory.title,
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
                    val isCurrent = category == currentCategory
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
                            onClickCategory(category)
                            expanded = false
                        })
                }
            }
        },
        actions = {
            IconButton(onClick = onClickAbout) {
                Icon(
                    painter = painterResource(Res.drawable.ic_info_circle_linear),
                    contentDescription = stringResource(Res.string.about),
                )
            }
        }
    )
}

@Composable
fun PaginatedItemList(
    loading: Boolean,
    currentPage: Int,
    itemIds: ImmutableList<Long>,
    items: ImmutableList<Item>,
    seenItemsIds: ImmutableSet<String>,
    onClickItem: (Item) -> Unit,
    onClickComment: (Item) -> Unit,
    onLoadNextPage: () -> Unit,
    onMarkItemAsSeen: (Item) -> Unit,
    contentPadding: PaddingValues = PaddingValues.Zero,
) {
    val listState = rememberLazyListState()

    if (itemIds.isEmpty()) {
        onLoadNextPage()
    } else {
        LaunchedEffect(listState) {
            snapshotFlow { listState.layoutInfo.visibleItemsInfo }
                .mapNotNull { visibleItems -> visibleItems.lastOrNull()?.index }
                .collect { lastVisibleItemIndex ->
                    if (lastVisibleItemIndex >= items.size - MainViewModel.PAGE_SIZE / 2) {
                        onLoadNextPage()
                    }
                }
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = contentPadding
    ) {
        items(items = items, key = { item -> item.getItemId() }) { item ->
            ItemRowWidget(
                item = item,
                seen = seenItemsIds.contains(item.getItemId().toString()),
                onClickItem = {
                    onMarkItemAsSeen(item)
                    onClickItem(item)
                },
                onClickComment = {
                    onMarkItemAsSeen(item)
                    onClickComment(item)
                },
            )
        }

        if (items.isNotEmpty() && currentPage * MainViewModel.PAGE_SIZE < itemIds.size) {
            // only display the loading item if there are items loaded
            item { ItemLoadingWidget() }
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
