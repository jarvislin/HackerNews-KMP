import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalWindowInfo
import platform.UIKit.UIDevice

class IOSPlatform : Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    override fun getScreenWidth(): Float =
        LocalWindowInfo.current.containerSize.width.toFloat()

    override fun isAndroid(): Boolean = false
}

actual fun getPlatform(): Platform = IOSPlatform()