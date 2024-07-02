import androidx.compose.runtime.Composable

interface Platform {
    val name: String
    @Composable
    fun getScreenWidth(): Float
    fun isAndroid(): Boolean
}

expect fun getPlatform(): Platform