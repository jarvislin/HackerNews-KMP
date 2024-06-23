@file:OptIn(ExperimentalMaterial3Api::class)

package presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import domain.models.Category
import domain.models.Item
import domain.models.getCommentCount
import domain.models.getFormatedTime
import domain.models.getPoint
import domain.models.getTitle
import domain.models.getUrl
import hackernewskmp.composeapp.generated.resources.Res
import hackernewskmp.composeapp.generated.resources.chevron_down
import hackernewskmp.composeapp.generated.resources.clock
import hackernewskmp.composeapp.generated.resources.link
import hackernewskmp.composeapp.generated.resources.message
import io.ktor.http.Url
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.mapNotNull
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import presentation.viewmodels.MainViewModel

class MainScreen : Screen {
    @Composable
    override fun Content() {
        Scaffold(
            topBar = { AppTopBar() }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = padding.calculateTopPadding(),
                        bottom = padding.calculateBottomPadding()
                    )
            ) {
                PaginatedItemList()
            }
        }
    }
}

@Composable
fun AppTopBar(viewModel: MainViewModel = koinInject()) {
    var expanded by remember { mutableStateOf(false) }
    var selectedItem by viewModel.currentCategory

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(8.dp)
            .zIndex(1f)
            .clickable(onClick = { expanded = true })
    ) {
        ElevatedButton(
            onClick = { expanded = true },
            modifier = Modifier.wrapContentSize()
        ) {
            Row {
                Text(
                    text = selectedItem.title,
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize
                )
                Icon(
                    modifier = Modifier.padding(start = 6.dp).align(Alignment.CenterVertically),
                    painter = painterResource(Res.drawable.chevron_down),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = null,
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            Category.getAll().forEach { item ->
                DropdownMenuItem(
                    text = { Text(text = item.title) },
                    onClick = {
                        if (selectedItem != item) {
                            selectedItem = item
                            viewModel.reset()
                        }
                        expanded = false
                    })
            }
        }
    }
}

@Composable
fun PaginatedItemList(
    viewModel: MainViewModel = koinInject()
) {
    val refreshState = rememberPullToRefreshState()
    val listState = rememberLazyListState()
    val itemIds by viewModel.itemIds
    val items by viewModel.items
    val currentPage by viewModel.currentPage
    val isLoading by viewModel.isLoading

    if (refreshState.isRefreshing) {
        LaunchedEffect(true) {
            delay(600)
            viewModel.reset()
            refreshState.endRefresh()
        }
    }

    if (itemIds.isEmpty()) {
        viewModel.loadNextPage()
    } else {
        LaunchedEffect(listState) {
            snapshotFlow { listState.layoutInfo.visibleItemsInfo }
                .mapNotNull { visibleItems -> visibleItems.lastOrNull()?.index }
                .collect { lastVisibleItemIndex ->
                    if (lastVisibleItemIndex >= items.size - viewModel.pageSize / 2) {
                        viewModel.loadNextPage()
                    }
                }
        }
    }

    Box(Modifier.nestedScroll(refreshState.nestedScrollConnection)) {
        if (isLoading && items.isEmpty()) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        }
        LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
            items(items.size) { index ->
                ItemRowWidget(items[index])
                HorizontalDivider(thickness = 6.dp, color = MaterialTheme.colorScheme.surfaceContainerLow)
            }

            if (items.isNotEmpty() && currentPage * viewModel.pageSize < itemIds.size) {
                // only display the loading item if there are items loaded
                item { ItemLoadingWidget() }
            }
        }
        PullToRefreshContainer(refreshState, Modifier.align(Alignment.TopCenter))
    }
}

@Composable
fun ItemRowWidget(item: Item) {
    val navigator = LocalNavigator.currentOrThrow
    Column(
        Modifier.fillMaxWidth()
            .clickable { navigator.push(DetailsScreen()) }
    ) {
        Spacer(Modifier.size(6.dp))
        Text(
            item.getTitle(),
            Modifier.padding(horizontal = 8.dp),
            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
            fontFamily = MaterialTheme.typography.headlineSmall.fontFamily
        )
        item.getUrl()?.let { url ->
            Row {
                Icon(
                    painter = painterResource(Res.drawable.link),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = 8.dp)
                        .align(Alignment.CenterVertically)
                )
                Text(
                    Url(url).host,
                    Modifier.padding(horizontal = 4.dp),
                    fontSize = MaterialTheme.typography.bodySmall.fontSize,
                )
            }

        }
        Row {
            Text(
                "${item.getPoint()} points",
                Modifier.padding(start = 8.dp),
                fontSize = MaterialTheme.typography.bodySmall.fontSize
            )
            item.getCommentCount()?.let { count ->
                Icon(
                    painter = painterResource(Res.drawable.message),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = 12.dp).align(Alignment.CenterVertically)
                )
                Text(
                    count.toString(), fontSize = MaterialTheme.typography.bodySmall.fontSize,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
            Icon(
                painter = painterResource(Res.drawable.clock),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = 12.dp).align(Alignment.CenterVertically)
            )
            Text(
                item.getFormatedTime(), fontSize = MaterialTheme.typography.bodySmall.fontSize,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
        Spacer(Modifier.size(6.dp))
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
            text = "Loading...",
            fontStyle = MaterialTheme.typography.bodySmall.fontStyle,
            fontSize = MaterialTheme.typography.bodySmall.fontSize,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
    }
}
