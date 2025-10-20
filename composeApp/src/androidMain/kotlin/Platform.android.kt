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
import ui.appTypography
import ui.darkScheme
import ui.lightScheme

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
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

actual fun getPlatform(): Platform = AndroidPlatform()