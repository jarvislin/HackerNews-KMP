import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable

interface Platform {
    val name: String

    @Composable
    fun getScreenWidth(): Float
    fun isAndroid(): Boolean
    @Composable
    fun getTypography(): Typography
}

expect fun getPlatform(): Platform