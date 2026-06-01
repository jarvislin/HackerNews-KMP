@file:OptIn(ExperimentalMaterial3Api::class)

package presentation.screens.details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    visible: Boolean,
    modifier: Modifier = Modifier,
    defaultBackgroundColor: Color = MaterialTheme.colorScheme.background,
) {
    val webViewNavigator = rememberWebViewNavigator(
        requestInterceptor = getPlatform().webRequestInterceptor()
    )
    val webViewState = rememberWebViewState(
        url = wrapUrl(url),
        extraSettings = {
            backgroundColor = Color.White
            iOSWebSettings.underPageBackgroundColor = defaultBackgroundColor
        }
    )
    Box(modifier = modifier.fillMaxSize()) {
        WebView(
            navigator = webViewNavigator,
            captureBackPresses = visible,
            state = webViewState,
            modifier = Modifier
                .fillMaxSize()
        )
        if (webViewState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
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
