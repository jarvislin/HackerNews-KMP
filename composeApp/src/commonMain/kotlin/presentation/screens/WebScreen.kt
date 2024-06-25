@file:OptIn(ExperimentalMaterial3Api::class)

package presentation.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.WebViewNavigator
import com.multiplatform.webview.web.rememberWebViewNavigator
import com.multiplatform.webview.web.rememberWebViewState
import domain.models.Item
import domain.models.getCommentCount
import domain.models.getUrl
import hackernewskmp.composeapp.generated.resources.Res
import hackernewskmp.composeapp.generated.resources.arrow_back
import hackernewskmp.composeapp.generated.resources.message
import hackernewskmp.composeapp.generated.resources.reload
import hackernewskmp.composeapp.generated.resources.world
import io.ktor.http.Url
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject

class WebScreen(private val itemJson: String) : Screen {
    @Composable
    override fun Content() {
        val json = koinInject<Json>()
        val item = Item.from(json, itemJson) ?: throw IllegalStateException("Item is null")
        val snackBarHostState = remember { SnackbarHostState() }
        val webViewState = rememberWebViewState(item.getUrl()!!)
        val webViewNavigator = rememberWebViewNavigator()
        Scaffold(
            snackbarHost = { SnackbarHost(snackBarHostState) },
            topBar = { WebTopBar(item, webViewNavigator) },
        ) { paddings ->
            WebView(
                navigator = webViewNavigator,
                state = webViewState,
                modifier = Modifier.fillMaxSize().padding(
                    top = paddings.calculateTopPadding(),
                    bottom = paddings.calculateBottomPadding()
                )
            )
        }

        // handle the message when the URL is a pdf file
        item.getUrl()?.let {
            Url(it).pathSegments.lastOrNull()?.takeIf { it.endsWith(".pdf") }?.let {
                LaunchedEffect(Unit) {
                    snackBarHostState.showSnackbar("PDF file is unsupported", "Dismiss")
                }
            }
        }
    }
}

@Composable
fun WebTopBar(item: Item, webViewNavigator: WebViewNavigator) {
    val localUriHandler = LocalUriHandler.current
    val navigator = LocalNavigator.currentOrThrow
    val json = koinInject<Json>()
    TopAppBar(
        title = {
            Text(
                text = Url(item.getUrl()!!).host,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                fontFamily = MaterialTheme.typography.bodyLarge.fontFamily
            )
        },
        navigationIcon = {
            IconButton(onClick = { navigator.pop() }) {
                Icon(
                    painter = painterResource(Res.drawable.arrow_back),
                    contentDescription = "Go back"
                )
            }
        },
        actions = {
            IconButton(onClick = { webViewNavigator.reload() }) {
                Icon(
                    painter = painterResource(Res.drawable.reload),
                    contentDescription = "Reload the web page"
                )
            }
            IconButton(
                onClick = { localUriHandler.openUri(item.getUrl() ?: throw IllegalStateException("URL is null")) },
            ) {
                Icon(
                    painter = painterResource(Res.drawable.world),
                    contentDescription = "Open with the default browser"
                )
            }
            item.getCommentCount()?.let { count ->
                IconButton(
                    onClick = { navigator.push(DetailsScreen(item.toJson(json))) },
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.message),
                        contentDescription = "Browse comments",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    )
}
