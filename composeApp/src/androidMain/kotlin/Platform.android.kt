import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Typography
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.jarvislin.hackernews.BuildConfig
import com.jarvislin.hackernews.HnKmp
import com.jarvislin.hackernews.R
import com.multiplatform.webview.request.RequestInterceptor
import com.multiplatform.webview.request.WebRequest
import com.multiplatform.webview.request.WebRequestInterceptResult
import com.multiplatform.webview.web.WebViewNavigator
import io.github.aakira.napier.Napier
import okio.Path.Companion.toPath
import ui.appTypography
import ui.darkScheme
import ui.lightScheme
import utils.Constants.DATASTORE_FILE_NAME

class AndroidPlatform(private val context: Context) : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"

    override val appName: String = context.getString(R.string.app_name)

    override val appVersionName: String = BuildConfig.VERSION_NAME

    override val appVersionCode: Int = BuildConfig.VERSION_CODE

    override fun createDataStore(): DataStore<Preferences> =
        PreferenceDataStoreFactory.createWithPath(
            produceFile = { context.filesDir.resolve(DATASTORE_FILE_NAME).absolutePath.toPath() }
        )

    override fun webRequestInterceptor(): RequestInterceptor =
        object: RequestInterceptor {
            override fun onInterceptUrlRequest(
                request: WebRequest,
                navigator: WebViewNavigator
            ): WebRequestInterceptResult {
                if (request.url.startsWith("intent://")) {
                    try {
                        val intent = Intent.parseUri(request.url, Intent.URI_INTENT_SCHEME)
                        context.startActivity(intent)
                        return WebRequestInterceptResult.Reject
                    } catch (e: Exception) {
                        Napier.i("Failed to parse and start intent", e)
                    }
                }
                return WebRequestInterceptResult.Allow
            }
        }

    override fun share(title: String, text: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share via").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }

    override fun getDefaultBrowserName(urlString: String): String? {
        val intent = Intent(Intent.ACTION_VIEW, urlString.toUri())
        val resolveInfo = context.packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
        val pkgName = resolveInfo?.activityInfo?.packageName ?: return null
        return try {
            val appInfo = context.packageManager.getApplicationInfo(pkgName, 0)
            context.packageManager.getApplicationLabel(appInfo).toString()
        } catch (e: Exception) {
            Napier.i("Could not get application info", e)
            null
        }
    }

    @Composable
    override fun getScreenWidth(): Float =
        with(LocalDensity.current) { LocalConfiguration.current.screenWidthDp.dp.toPx() }

    override fun isAndroid(): Boolean = true

    @Composable
    override fun getTypography(): Typography = appTypography()

    @Composable
    override fun getColorScheme(darkTheme: Boolean): ColorScheme {
        val ctx = LocalContext.current
        val supportsDynamic = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
        return when {
            supportsDynamic && darkTheme -> dynamicDarkColorScheme(ctx)
            supportsDynamic && !darkTheme -> dynamicLightColorScheme(ctx)
            darkTheme -> darkScheme
            else -> lightScheme
        }
    }
}

actual fun getPlatform(): Platform = AndroidPlatform(HnKmp.instance)