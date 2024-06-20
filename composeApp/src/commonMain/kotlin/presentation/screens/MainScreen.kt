@file:OptIn(ExperimentalMaterial3Api::class)

package presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
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
fun AppTopBar() {
    TopAppBar(title = { Text("Hacker News") })
}

@Composable
fun PaginatedItemList(viewModel: MainViewModel = koinInject()) {
    val itemIds by viewModel.itemIds
    val items by viewModel.items
    val currentPage by viewModel.currentPage

    if (itemIds.isEmpty()) {
        viewModel.loadNextPage()
    }

    LazyColumn {
        items(items.size) { item ->
            Text(item.toString())
        }

        if (currentPage * viewModel.pageSize < itemIds.size) {
            item {
                Button(onClick = { viewModel.loadNextPage() }) {
                    Text("Load More")
                }
            }
        }
    }
}