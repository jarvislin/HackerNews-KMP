import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalWindowInfo
import platform.UIKit.UIDevice
import ui.baseline
import ui.darkScheme
import ui.lightScheme

@ExperimentalComposeUiApi
class IOSPlatform : Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion

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