package ui

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import hackernewskmp.composeapp.generated.resources.Res
import hackernewskmp.composeapp.generated.resources.bitter_regular
import org.jetbrains.compose.resources.Font


@Composable
fun displayFontFamily() = FontFamily(
    Font(resource = Res.font.bitter_regular)
)

// Default Material 3 typography values
val baseline = Typography()

@Composable
fun appTypography() = Typography(
    displayLarge = baseline.displayLarge.copy(fontFamily = displayFontFamily()),
    displayMedium = baseline.displayMedium.copy(fontFamily = displayFontFamily()),
    displaySmall = baseline.displaySmall.copy(fontFamily = displayFontFamily()),
    headlineLarge = baseline.headlineLarge.copy(fontFamily = displayFontFamily()),
    headlineMedium = baseline.headlineMedium.copy(fontFamily = displayFontFamily()),
    headlineSmall = baseline.headlineSmall.copy(fontFamily = displayFontFamily()),
    titleLarge = baseline.titleLarge.copy(fontFamily = displayFontFamily()),
    titleMedium = baseline.titleMedium.copy(fontFamily = displayFontFamily()),
    titleSmall = baseline.titleSmall.copy(fontFamily = displayFontFamily()),
)
