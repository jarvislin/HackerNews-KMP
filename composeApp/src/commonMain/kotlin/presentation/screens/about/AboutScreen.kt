package presentation.screens.about

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import getPlatform
import hackernewskmp.composeapp.generated.resources.Res
import hackernewskmp.composeapp.generated.resources.about
import hackernewskmp.composeapp.generated.resources.app_icon_content_description
import hackernewskmp.composeapp.generated.resources.back_button_content_description
import hackernewskmp.composeapp.generated.resources.ic_arrow_left_linear
import hackernewskmp.composeapp.generated.resources.ic_launcher_mono
import hackernewskmp.composeapp.generated.resources.ic_square_top_down_linear
import hackernewskmp.composeapp.generated.resources.open_source_libraries
import hackernewskmp.composeapp.generated.resources.version_version_author
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import presentation.widgets.RowOrColumnLayout
import sv.lib.squircleshape.SquircleShape
import ui.HnColor

private const val AUTHOR = "Jarvis Lin"
private const val GITHUB_URL = "https://github.com/jarvislin/HackerNews-KMP"

@Serializable
object AboutRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onBack: () -> Unit) {
    val uriHandler = LocalUriHandler.current
    val appVersionName = getPlatform().appVersionName
    val appVersionCode = getPlatform().appVersionCode.toString()
    val appName = getPlatform().appName
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(Res.string.about)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_arrow_left_linear),
                            contentDescription = stringResource(Res.string.back_button_content_description),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors().run { copy(containerColor = containerColor.copy(alpha = 0.9f)) },
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Box(
                modifier = Modifier
                    .size(108.dp)
            ) {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .padding((108.dp - 72.dp) / 2)
                    .background(color = HnColor.launcherBackground, shape = SquircleShape())
                )
                Icon(
                    painter = painterResource(Res.drawable.ic_launcher_mono),
                    contentDescription = stringResource(Res.string.app_icon_content_description),
                    tint = HnColor.launcherForeground,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(Modifier.height(16.dp))
            Text(
                text = appName,
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))

            Text(
                text = stringResource(
                    Res.string.version_version_author,
                    appVersionName,
                    appVersionCode,
                    AUTHOR
                ),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            TextButton(onClick = { uriHandler.openUri(GITHUB_URL) }) {
                Text(text = GITHUB_URL)
            }
            Spacer(Modifier.height(24.dp))
            Text(
                text = stringResource(Res.string.open_source_libraries),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(Modifier.height(8.dp))
            Column(
                verticalArrangement = spacedBy(16.dp),
            ) {
                openSourceLibraries.forEach { library ->
                    OpenSourceLibraryRow(
                        library = library,
                        onClickProjectUrl = { uriHandler.openUri(library.projectUrl) },
                        onClickLicenseUrl = { uriHandler.openUri(library.licenseUrl) },
                        modifier = Modifier.align(Alignment.Start)
                    )
                }
            }
        }
    }
}

@Composable
private fun OpenSourceLibraryRow(
    library: OpenSourceLibrary,
    onClickProjectUrl: () -> Unit,
    onClickLicenseUrl: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceContainer, shape = RoundedCornerShape(8.dp))
    ) {
        RowOrColumnLayout(
            primary = {
                TextButton(
                    onClick = onClickProjectUrl,
                ) {
                    Icon(
                        painterResource(Res.drawable.ic_square_top_down_linear),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = library.name,
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            },
            secondary = {
                TextButton(
                    onClick = onClickLicenseUrl,
                ) {
                    Text(text = library.license)
                }
            }
        )
        Text(
            text = library.description,
            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp),
            modifier = Modifier.padding(horizontal = 12.dp).padding(bottom = 12.dp)
        )
    }
}

@Preview(name = "iPhone SE (250dp)", widthDp = 250)
@Preview(name = "Narrow (360dp)", widthDp = 360)
@Preview(name = "Wide (800dp)", widthDp = 800)
@Composable
private fun OpenSourceLibraryRowPreview() {
    val library = OpenSourceLibrary(
        name = "Compose MP Webview",
        description = "In-app browser support for article rendering.",
        license = "Apache 2.0",
        licenseUrl = "https://github.com/JetBrains/compose-multiplatform/blob/master/LICENSE",
        projectUrl = "https://www.jetbrains.com/lp/compose-multiplatform/",
    )
    MaterialTheme {
        OpenSourceLibraryRow(
            library = library,
            onClickProjectUrl = {},
            onClickLicenseUrl = {},
        )
    }
}

private data class OpenSourceLibrary(
    val name: String,
    val description: String,
    val license: String,
    val licenseUrl: String,
    val projectUrl: String,
)

private val openSourceLibraries = listOf(
    OpenSourceLibrary(
        name = "Coil + Ktor3",
        description = "Image loading for favicons, avatars, and inline images.",
        license = "Apache 2.0",
        licenseUrl = "https://github.com/coil-kt/coil/blob/main/LICENSE.txt",
        projectUrl = "https://coil-kt.github.io/coil/compose/",
    ),
    OpenSourceLibrary(
        name = "Compose MP WebView",
        description = "In-app browser support for article rendering.",
        license = "Apache 2.0",
        licenseUrl = "https://github.com/KevinnZou/compose-webview-multiplatform/blob/main/LICENSE.txt",
        projectUrl = "https://github.com/KevinnZou/compose-webview-multiplatform",
    ),
    OpenSourceLibrary(
        name = "Compose Multiplatform",
        description = "UI toolkit used for Android and iOS screens.",
        license = "Apache 2.0",
        licenseUrl = "https://github.com/JetBrains/compose-multiplatform/blob/master/LICENSE",
        projectUrl = "https://www.jetbrains.com/lp/compose-multiplatform/",
    ),
    OpenSourceLibrary(
        name = "HtmlConverterCompose",
        description = "Provides a simple API to convert HTML to Compose's AnnotatedString",
        license = "Apache 2.0",
        licenseUrl = "https://github.com/cbeyls/HtmlConverterCompose/blob/main/LICENSE",
        projectUrl = "https://github.com/cbeyls/HtmlConverterCompose",
    ),
    OpenSourceLibrary(
        name = "Koin",
        description = "Dependency injection used by view models and repositories.",
        license = "Apache 2.0",
        licenseUrl = "https://github.com/InsertKoinIO/koin/blob/main/LICENSE",
        projectUrl = "https://insert-koin.io",
    ),
    OpenSourceLibrary(
        name = "Kotlin",
        description = "Language + stdlib used across every module.",
        license = "Apache 2.0",
        licenseUrl = "https://github.com/JetBrains/kotlin/blob/master/license/LICENSE.txt",
        projectUrl = "https://kotlinlang.org",
    ),
    OpenSourceLibrary(
        name = "Kotlin Coroutines",
        description = "Asynchronous pipelines for networking and caching.",
        license = "Apache 2.0",
        licenseUrl = "https://github.com/Kotlin/kotlinx.coroutines/blob/master/LICENSE.txt",
        projectUrl = "https://github.com/Kotlin/kotlinx.coroutines",
    ),
    OpenSourceLibrary(
        name = "Ktor Client",
        description = "Networking client that hits the Hacker News API.",
        license = "Apache 2.0",
        licenseUrl = "https://github.com/ktorio/ktor/blob/main/LICENSE",
        projectUrl = "https://ktor.io",
    ),
    OpenSourceLibrary(
        name = "kotlinx-datetime",
        description = "Time formatting utilities shown across story metadata.",
        license = "Apache 2.0",
        licenseUrl = "https://github.com/Kotlin/kotlinx-datetime/blob/master/LICENSE.txt",
        projectUrl = "https://github.com/Kotlin/kotlinx-datetime",
    ),
    OpenSourceLibrary(
        name = "kotlinx-serialization",
        description = "JSON serialization for API requests and local cache.",
        license = "Apache 2.0",
        licenseUrl = "https://github.com/Kotlin/kotlinx.serialization/blob/master/LICENSE.txt",
        projectUrl = "https://github.com/Kotlin/kotlinx.serialization",
    ),
    OpenSourceLibrary(
        name = "Napier",
        description = "Multiplatform logging used for diagnostics.",
        license = "Apache 2.0",
        licenseUrl = "https://github.com/AAkira/Napier/blob/master/LICENSE",
        projectUrl = "https://github.com/AAkira/Napier",
    ),
    OpenSourceLibrary(
        name = "Squircle Shape",
        description = "A Compose Multiplatform library providing customizable Squircle shapes for UI components.",
        license = "MIT",
        licenseUrl = "https://github.com/stoyan-vuchev/squircle-shape/blob/master/LICENSE",
        projectUrl = "https://github.com/stoyan-vuchev/squircle-shape",
    ),
).sortedBy { it.name }
