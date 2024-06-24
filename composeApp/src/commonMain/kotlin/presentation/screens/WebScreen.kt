package presentation.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.WebViewNavigator
import com.multiplatform.webview.web.rememberWebViewNavigator
import com.multiplatform.webview.web.rememberWebViewState
import domain.models.Item
import domain.models.getUrl
import hackernewskmp.composeapp.generated.resources.Res
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
            bottomBar = { BottomBar(item, webViewNavigator) },
        ) {
            WebView(
                navigator = webViewNavigator,
                state = webViewState,
                modifier = Modifier.fillMaxSize()
            )
        }

        // handle the message when the url is a pdf file
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
fun BottomBar(item: Item, webViewNavigator: WebViewNavigator) {
    val localUriHandler = LocalUriHandler.current
    BottomAppBar(modifier = Modifier.height(56.dp)) {
        Row {
            Spacer(modifier = Modifier.weight(1f))
            IconButton(modifier = Modifier.align(Alignment.CenterVertically),
                onClick = { webViewNavigator.reload() }) {
                Icon(
                    painterResource(Res.drawable.reload),
                    contentDescription = "Reload the web page"
                )
            }
            IconButton(
                modifier = Modifier.align(Alignment.CenterVertically),
                onClick = { localUriHandler.openUri(item.getUrl() ?: throw IllegalStateException("URL is null")) },
            ) {
                Icon(
                    painterResource(Res.drawable.world),
                    contentDescription = "Open with the default browser"
                )
            }
        }
    }
}
