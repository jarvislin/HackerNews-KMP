@file:OptIn(ExperimentalMaterial3Api::class)

package presentation.screens.details

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.WebViewNavigator
import com.multiplatform.webview.web.WebViewState
import com.multiplatform.webview.web.rememberWebViewNavigator
import com.multiplatform.webview.web.rememberWebViewState
import extensions.isPdf
import extensions.toUrl
import getPlatform
import utils.Constants

@Composable
fun WebviewTabContent(
    url: String,
    modifier: Modifier = Modifier,
    webViewNavigator: WebViewNavigator = rememberWebViewNavigator(),
    webViewState: WebViewState = rememberWebViewState(wrapUrl(url)),
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(modifier = modifier.fillMaxSize()) {
        WebView(
            navigator = webViewNavigator,
            state = webViewState,
            modifier = Modifier
                .fillMaxSize()
        )
        if (webViewState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
    Spacer(
        modifier = Modifier.fillMaxHeight().width(20.dp)
            .background(Color.Transparent)
            .clickable(interactionSource = interactionSource, indication = null) {
                // this is a workaround to fix the issue with the WebView not being swipe-able on iOS
            })
}

/**
 * Some URL's need to be wrapped to be viewable. For instance, to view a PDF,
 * it can be wrapped to be viewed from google docs.
 */
private fun wrapUrl(rawUrl: String): String =
    when {
        (getPlatform().isAndroid() && rawUrl.toUrl()?.isPdf() == true) -> Constants.URL_GOOGLE_DOCS + rawUrl
        else -> rawUrl
    }
