import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.multiplatform.webview.request.RequestInterceptor

interface Platform {
    val name: String
    val appName: String
    val appVersion: String

    /**
     *   Gets the singleton DataStore instance, creating it if necessary.
     */
    fun createDataStore(): DataStore<Preferences>

    fun webRequestInterceptor(): RequestInterceptor?

    fun share(title: String, text: String)

    fun getDefaultBrowserName(urlString: String): String?

    @Composable
    fun getScreenWidth(): Float
    fun isAndroid(): Boolean
    @Composable
    fun getTypography(): Typography
    @Composable
    fun getColorScheme(darkTheme: Boolean): ColorScheme
}

expect fun getPlatform(): Platform