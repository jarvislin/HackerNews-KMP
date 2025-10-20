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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
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
import hackernewskmp.composeapp.generated.resources.browse_comments
import hackernewskmp.composeapp.generated.resources.go_back
import hackernewskmp.composeapp.generated.resources.message
import hackernewskmp.composeapp.generated.resources.open_with_the_default_browser
import hackernewskmp.composeapp.generated.resources.reload
import hackernewskmp.composeapp.generated.resources.reload_web_page
import hackernewskmp.composeapp.generated.resources.webview_error
import hackernewskmp.composeapp.generated.resources.world
import io.github.aakira.napier.Napier
import io.ktor.http.Url
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import presentation.viewmodels.MainViewModel
import utils.Constants

@Serializable
data class WebRoute(
    @SerialName("id")
    val id: Long
)

@Composable
fun WebScreen(itemId: Long, onBack: () -> Unit, onClickComment: (Item) -> Unit) {
    val snackBarHostState = remember { SnackbarHostState() }
    val webViewNavigator = rememberWebViewNavigator()
    val mainViewModel = koinInject<MainViewModel>()
    val item = mainViewModel.state.value.items.first { it.getItemId() == itemId }
    val rawUrl = item.getUrl()
        ?: throw IllegalStateException(Constants.URL_NULL_MESSAGE)
    val webViewState = rememberWebViewState(getUrl(rawUrl))
    val scope = rememberCoroutineScope()

    ScaffoldContent(snackBarHostState, item, webViewNavigator, webViewState, onBack, onClickComment)

    webViewState.errorsForCurrentRequest.forEach { error ->
        scope.launch(Dispatchers.Main) {
            Napier.e(getString(Res.string.webview_error, error.description))
        }
    }


}

private fun getUrl(rawUrl: String): String {
    val isPdf = Url(rawUrl).pathSegments.lastOrNull()
        ?.endsWith(Constants.PDF_EXTENSION) ?: false
    return if (isPdf && getPlatform().isAndroid()) {
        Constants.URL_GOOGLE_DOCS + rawUrl
    } else rawUrl
}

@Composable
fun ScaffoldContent(
    snackBarHostState: SnackbarHostState,
    item: Item, webViewNavigator: WebViewNavigator, webViewState: WebViewState,
    onBack: () -> Unit, onClickComment: (Item) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = { WebTopBar(item, webViewNavigator, onBack, onClickComment) },
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
fun WebTopBar(item: Item, webViewNavigator: WebViewNavigator, onBack: () -> Unit, onClickComment: (Item) -> Unit) {
    val localUriHandler = LocalUriHandler.current
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
            IconButton(onClick = { onBack() }) {
                Icon(
                    painter = painterResource(Res.drawable.arrow_back),
                    contentDescription = stringResource(Res.string.go_back)
                )
            }
        },
        actions = {
            IconButton(onClick = { webViewNavigator.reload() }) {
                Icon(
                    painter = painterResource(Res.drawable.reload),
                    contentDescription = stringResource(Res.string.reload_web_page)
                )
            }
            IconButton(
                onClick = {
                    localUriHandler.openUri(
                        item.getUrl() ?: throw IllegalStateException(
                            Constants.URL_NULL_MESSAGE
                        )
                    )
                },
            ) {
                Icon(
                    painter = painterResource(Res.drawable.world),
                    contentDescription = stringResource(Res.string.open_with_the_default_browser)
                )
            }
            item.getCommentCount()?.let {
                IconButton(
                    onClick = { onClickComment(item) },
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.message),
                        contentDescription = stringResource(Res.string.browse_comments),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    )
}
