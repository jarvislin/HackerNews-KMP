import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath

interface Platform {
    val name: String

    /**
     *   Gets the singleton DataStore instance, creating it if necessary.
     */
    fun createDataStore(): DataStore<Preferences>

    @Composable
    fun getScreenWidth(): Float
    fun isAndroid(): Boolean
    @Composable
    fun getTypography(): Typography
    @Composable
    fun getColorScheme(darkTheme: Boolean): ColorScheme
}

expect fun getPlatform(): Platform