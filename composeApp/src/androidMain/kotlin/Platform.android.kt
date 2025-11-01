import android.content.Context
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
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.jarvislin.hackernews.HnKmp
import okio.Path.Companion.toPath
import ui.appTypography
import ui.darkScheme
import ui.lightScheme
import utils.Constants.DATASTORE_FILE_NAME

class AndroidPlatform(private val context: Context) : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"

    override fun createDataStore(): DataStore<Preferences> =
        PreferenceDataStoreFactory.createWithPath(
            produceFile = { context.filesDir.resolve(DATASTORE_FILE_NAME).absolutePath.toPath() }
        )

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