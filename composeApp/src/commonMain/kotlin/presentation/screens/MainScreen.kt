@file:OptIn(ExperimentalMaterial3Api::class)

package presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import domain.models.Category
import domain.models.Item
import domain.models.getCommentCount
import domain.models.getFormatedDiffTime
import domain.models.getPoint
import domain.models.getTitle
import domain.models.getUrl
import hackernewskmp.composeapp.generated.resources.Res
import hackernewskmp.composeapp.generated.resources.an_error_occurred
import hackernewskmp.composeapp.generated.resources.chevron_down
import hackernewskmp.composeapp.generated.resources.clock
import hackernewskmp.composeapp.generated.resources.link
import hackernewskmp.composeapp.generated.resources.loading
import hackernewskmp.composeapp.generated.resources.message
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
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = state.refreshing,
            onRefresh = viewModel::onPullToRefresh,
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            PaginatedItemList(onClickItem, onClickComment)
        }
    }

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

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(MaterialTheme.colorScheme.surface)
            .zIndex(1f)
    ) {
        Row(modifier = Modifier.fillMaxHeight().clickable { expanded = true }) {
            Spacer(Modifier.size(16.dp))
            Text(
                modifier = Modifier.align(Alignment.CenterVertically),
                text = state.currentCategory.title,
                color = MaterialTheme.colorScheme.primary,
                fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                fontFamily = MaterialTheme.typography.headlineSmall.fontFamily,
            )
            Icon(
                modifier = Modifier.padding(start = 8.dp).align(Alignment.CenterVertically),
                painter = painterResource(Res.drawable.chevron_down),
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = null,
            )
            Spacer(Modifier.size(16.dp))
        }
        DropdownMenu(
            offset = DpOffset(16.dp, 0.dp),
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            Category.getAll().forEach { item ->
                DropdownMenuItem(
                    text = { Text(text = item.title) },
                    onClick = {
                        viewModel.onClickCategory(item)
                        expanded = false
                    })
            }
        }
    }
}

@Composable
fun PaginatedItemList(
    onClickItem: (Item) -> Unit,
    onClickComment: (Item) -> Unit,
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
        LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
            items(state.items.size) { index ->
                ItemRowWidget(state.items[index], onClickItem, onClickComment)
            }

            if (state.items.isNotEmpty() && state.currentPage * MainViewModel.PAGE_SIZE < state.itemIds.size) {
                // only display the loading item if there are items loaded
                item { ItemLoadingWidget() }
            }
        }
    }
}

@Composable
fun ItemRowWidget(item: Item, onClickItem: (Item) -> Unit, onClickComment: (Item) -> Unit) {
    Column(
        Modifier.fillMaxWidth().clickable { onClickItem(item) }
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
            item.getUrl()?.let { url ->
                Icon(
                    painter = painterResource(Res.drawable.link),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    Url(url).host,
                    Modifier.padding(horizontal = 4.dp),
                    fontSize = MaterialTheme.typography.bodySmall.fontSize,
                    style = trimmedTextStyle
                )
            }
            Icon(
                painter = painterResource(Res.drawable.clock),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = if (item.getUrl() == null) 0.dp else 8.dp)
            )
            Text(
                item.getFormatedDiffTime(), fontSize = MaterialTheme.typography.bodySmall.fontSize,
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
                        onClick = { onClickComment(item) }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.message),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface,
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
    Row(modifier = Modifier.fillMaxWidth().background(color = MaterialTheme.colorScheme.surfaceContainerLow)) {
        CircularProgressIndicator(
            strokeWidth = 2.dp,
            modifier = Modifier.padding(16.dp).size(12.dp)
        )
        Text(
            text = stringResource(Res.string.loading),
            fontStyle = MaterialTheme.typography.bodySmall.fontStyle,
            fontSize = MaterialTheme.typography.bodySmall.fontSize,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
    }
}
