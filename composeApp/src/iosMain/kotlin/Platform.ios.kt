import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.multiplatform.webview.request.RequestInterceptor
import com.multiplatform.webview.request.WebRequest
import com.multiplatform.webview.request.WebRequestInterceptResult
import com.multiplatform.webview.setting.PlatformWebSettings
import com.multiplatform.webview.web.WebViewNavigator
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import okio.Path.Companion.toPath
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSString
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask
import platform.Foundation.create
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIDevice
import ui.baseline
import ui.darkScheme
import ui.lightScheme
import utils.Constants.DATASTORE_FILE_NAME

@ExperimentalComposeUiApi
class IOSPlatform : Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion

    @OptIn(ExperimentalForeignApi::class)
    override fun createDataStore(): DataStore<Preferences> =
        PreferenceDataStoreFactory.createWithPath(
            produceFile = {
                val documentDirectory: NSURL? = NSFileManager.defaultManager.URLForDirectory(
                    directory = NSDocumentDirectory,
                    inDomain = NSUserDomainMask,
                    appropriateForURL = null,
                    create = false,
                    error = null,
                )
                val path = requireNotNull(documentDirectory).path + "/$DATASTORE_FILE_NAME"
                path.toPath()
            }
        )

    override fun webRequestInterceptor(): RequestInterceptor? = null

    @OptIn(BetaInteropApi::class)
    override fun share(title: String, text: String) {
        val activityItems = listOf(
            NSString.create(text)
        )

        val activityViewController = UIActivityViewController(
            activityItems = activityItems,
            applicationActivities = null
        )

        // Get the top-most view controller to present the activity view controller
        val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
        rootViewController?.presentViewController(activityViewController, animated = true, completion = null)
    }

    @Composable
    override fun getScreenWidth(): Float =
        LocalWindowInfo.current.containerSize.width.toFloat()

    override fun isAndroid(): Boolean = false

    @Composable
    override fun getTypography(): Typography = baseline

    @Composable
    override fun getColorScheme(darkTheme: Boolean): ColorScheme =
        if (darkTheme) {
            darkScheme
        } else {
            lightScheme
        }
}

@ExperimentalComposeUiApi
actual fun getPlatform(): Platform = IOSPlatform()