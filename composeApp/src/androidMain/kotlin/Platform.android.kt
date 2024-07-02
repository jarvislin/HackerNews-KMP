import android.os.Build
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import ui.appTypography

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
    @Composable
    override fun getScreenWidth(): Float =
        with(LocalDensity.current) { LocalConfiguration.current.screenWidthDp.dp.toPx() }

    override fun isAndroid(): Boolean = true

    @Composable
    override fun getTypography(): Typography = appTypography()
}

actual fun getPlatform(): Platform = AndroidPlatform()