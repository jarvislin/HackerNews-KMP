@file:OptIn(ExperimentalMaterial3Api::class)

package presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import domain.models.Ask
import domain.models.Category
import domain.models.Comment
import domain.models.Item
import domain.models.Job
import domain.models.Poll
import domain.models.PollOption
import domain.models.Story
import hackernewskmp.composeapp.generated.resources.Res
import hackernewskmp.composeapp.generated.resources.chevron_down
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
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
        PullToRefreshContainer(refreshState, Modifier.align(Alignment.TopCenter))
        LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
            items(items.size) { index ->
                ItemRow(items[index])
            }

            if (currentPage * viewModel.pageSize < itemIds.size) {
                item { ItemLoading() }
            }
        }
    }
}

private fun Item.getTitle(): String = when (this) {
    is Ask -> title
    is Job -> title
    is Poll -> title
    is Story -> title
    else -> throw IllegalStateException("Unsupported item type")
}

@Composable
fun ItemRow(item: Item) {
    Text(item.getTitle())
}

@Composable
fun ItemLoading() {
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