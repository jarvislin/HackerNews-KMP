@file:OptIn(ExperimentalMaterial3Api::class)

package presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.WebViewNavigator
import com.multiplatform.webview.web.WebViewState
import com.multiplatform.webview.web.rememberWebViewNavigator
import com.multiplatform.webview.web.rememberWebViewState
import domain.models.Item
import domain.models.getCommentCount
import domain.models.getUrl
import getPlatform
import hackernewskmp.composeapp.generated.resources.Res
import hackernewskmp.composeapp.generated.resources.arrow_back
import hackernewskmp.composeapp.generated.resources.message
import hackernewskmp.composeapp.generated.resources.reload
import hackernewskmp.composeapp.generated.resources.world
import io.github.aakira.napier.Napier
import io.ktor.http.Url
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import presentation.widgets.SwipeContainer

class WebScreen(private val itemJson: String) : Screen {
    @Composable
    override fun Content() {
        val json = koinInject<Json>()
        val item = Item.from(json, itemJson) ?: throw IllegalStateException("Item is null")
        val snackBarHostState = remember { SnackbarHostState() }
        val webViewNavigator = rememberWebViewNavigator()
        val navigator = LocalNavigator.currentOrThrow
        val rawUrl = item.getUrl() ?: throw IllegalStateException("URL is null")
        val webViewState = rememberWebViewState(getUrl(rawUrl))

        if (getPlatform().isAndroid()) {
            ScaffoldContent(snackBarHostState, item, webViewNavigator, webViewState)
        } else {
            SwipeContainer(
                onSwipeToDismiss = { navigator.pop() },
                swipeThreshold = getPlatform().getScreenWidth() / 3.5f,
            ) {
                ScaffoldContent(snackBarHostState, item, webViewNavigator, webViewState)
            }
        }

        webViewState.errorsForCurrentRequest.forEach { error ->
            Napier.e("WebView error: ${error.description}")
        }
    }

    private fun getUrl(rawUrl: String): String {
        val isPdf = Url(rawUrl).pathSegments.lastOrNull()?.endsWith(".pdf") ?: false
        return if (isPdf && getPlatform().isAndroid()) "https://docs.google.com/gview?embedded=true&url=$rawUrl"
        else rawUrl
    }
}

@Composable
fun ScaffoldContent(
    snackBarHostState: SnackbarHostState,
    item: Item, webViewNavigator: WebViewNavigator, webViewState: WebViewState
) {
    val interactionSource = remember { MutableInteractionSource() }
    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = { WebTopBar(item, webViewNavigator) },
    ) { paddings ->
        Box {
            WebView(
                navigator = webViewNavigator,
                state = webViewState,
                modifier = Modifier.fillMaxSize().padding(
                    top = paddings.calculateTopPadding(),
                    bottom = paddings.calculateBottomPadding()
                )
            )
            if (webViewState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
        Spacer(modifier = Modifier.fillMaxHeight().width(20.dp)
            .background(Color.Transparent)
            .clickable(interactionSource = interactionSource, indication = null) {
                // this is a workaround to fix the issue with the WebView not being swipe-able on iOS
            })
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
