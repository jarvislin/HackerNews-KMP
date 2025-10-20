import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable

interface Platform {
    val name: String

    @Composable
    fun getScreenWidth(): Float
    fun isAndroid(): Boolean
    @Composable
    fun getTypography(): Typography
    @Composable
    fun getColorScheme(darkTheme: Boolean): ColorScheme
}

expect fun getPlatform(): Platform