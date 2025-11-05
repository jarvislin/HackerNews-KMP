package ui

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import hackernewskmp.composeapp.generated.resources.Res
import hackernewskmp.composeapp.generated.resources.google_sans_code_regular
import hackernewskmp.composeapp.generated.resources.product_sans_bold
import hackernewskmp.composeapp.generated.resources.product_sans_italic
import hackernewskmp.composeapp.generated.resources.product_sans_regular
import org.jetbrains.compose.resources.Font


@Composable
fun googleSansCodeFontFamily() = FontFamily(
    Font(resource = Res.font.google_sans_code_regular),
)

@Composable
fun productSansFontFamily() = FontFamily(
    Font(resource = Res.font.product_sans_regular),
    Font(resource = Res.font.product_sans_italic, style = FontStyle.Italic),
    Font(resource = Res.font.product_sans_bold, weight = FontWeight.Bold)
)

// Default Material 3 typography values
val baseline = Typography()

// Fix for line height issue on iOS
// See: https://github.com/jarvislin/HackerNews-KMP/issues/15
val trimmedTextStyle = TextStyle(
    lineHeightStyle = LineHeightStyle(
        alignment = LineHeightStyle.Alignment.Proportional,
        trim = LineHeightStyle.Trim.Both
    )
)

@Composable
fun appTypography() = Typography(
    displayLarge = baseline.displayLarge.copy(fontFamily = productSansFontFamily()),
    displayMedium = baseline.displayMedium.copy(fontFamily = productSansFontFamily()),
    displaySmall = baseline.displaySmall.copy(fontFamily = productSansFontFamily()),
    headlineLarge = baseline.headlineLarge.copy(fontFamily = productSansFontFamily()),
    headlineMedium = baseline.headlineMedium.copy(fontFamily = productSansFontFamily()),
    headlineSmall = baseline.headlineSmall.copy(fontFamily = productSansFontFamily()),
    titleLarge = baseline.titleLarge.copy(fontFamily = productSansFontFamily()),
    titleMedium = baseline.titleMedium.copy(fontFamily = productSansFontFamily()),
    titleSmall = baseline.titleSmall.copy(fontFamily = productSansFontFamily()),
    bodyLarge = baseline.bodyLarge.copy(fontFamily = productSansFontFamily()),
    bodyMedium = baseline.bodyMedium.copy(fontFamily = productSansFontFamily()),
    bodySmall = baseline.bodySmall.copy(fontFamily = productSansFontFamily()),
    labelLarge = baseline.labelLarge.copy(fontFamily = productSansFontFamily()),
    labelMedium = baseline.labelMedium.copy(fontFamily = productSansFontFamily()),
    labelSmall = baseline.labelSmall.copy(fontFamily = productSansFontFamily()),
)
