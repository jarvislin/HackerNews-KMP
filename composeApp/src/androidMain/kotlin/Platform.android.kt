import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"

    @Composable
    override fun getScreenWidth(): Float =
        with(LocalDensity.current) { LocalConfiguration.current.screenWidthDp.dp.toPx() }

    override fun isAndroid(): Boolean = true
}

actual fun getPlatform(): Platform = AndroidPlatform()