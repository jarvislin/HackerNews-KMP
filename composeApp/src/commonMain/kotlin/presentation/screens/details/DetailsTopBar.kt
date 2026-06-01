package presentation.screens.details

import androidx.compose.foundation.border
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import extensions.toUrl
import extensions.trimmedHostName
import hackernewskmp.composeapp.generated.resources.Res
import hackernewskmp.composeapp.generated.resources.back_button_content_description
import hackernewskmp.composeapp.generated.resources.ic_arrow_left_linear
import hackernewskmp.composeapp.generated.resources.ic_chat_line_linear
import hackernewskmp.composeapp.generated.resources.ic_global_outline
import hackernewskmp.composeapp.generated.resources.ic_square_top_down_linear
import hackernewskmp.composeapp.generated.resources.x_comments
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import presentation.widgets.SquircleBadge
import ui.AppPreview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsTopBar(
    selectedTab: DetailsScreenTab,
    urlString: String?,
    commentCount: Int,
    onTabSelected: (DetailsScreenTab) -> Unit,
    onBack: () -> Unit,
    onClickOpenExternal: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val trimmedHostName = urlString?.toUrl()?.trimmedHostName()
    val commentsLabel = pluralStringResource(Res.plurals.x_comments, commentCount, commentCount)
    TopAppBar(
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors().run { copy(containerColor = containerColor.copy(alpha = 0.9f)) },
        title = {
            if (selectedTab == DetailsScreenTab.Comments || trimmedHostName == null) {
                Text(commentsLabel)
            }
            else {
                Text(
                    text = trimmedHostName,
                    overflow = TextOverflow.Ellipsis,
                    autoSize = TextAutoSize.StepBased(
                        maxFontSize = MaterialTheme.typography.titleLarge.fontSize
                    ),
                    maxLines = 1
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    painter = painterResource(Res.drawable.ic_arrow_left_linear),
                    contentDescription = stringResource(Res.string.back_button_content_description),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        actions = {
            if (selectedTab == DetailsScreenTab.Comments && trimmedHostName != null) {
                IconButton(onClick = {onTabSelected(DetailsScreenTab.Webview)}) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_global_outline),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            if (selectedTab == DetailsScreenTab.Webview) {
                BadgedBox(
                    badge = {
                        if (commentCount > 0) {
                            SquircleBadge(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier
                                    .graphicsLayer {
                                        translationX = (-10).dp.toPx()
                                        translationY = 8.dp.toPx()
                                    }
                            ) {
                                Text("$commentCount")
                            }
                        }
                    }
                ) {
                    IconButton(onClick = {onTabSelected(DetailsScreenTab.Comments)}) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_chat_line_linear),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
            IconButton(onClick = onClickOpenExternal) {
                Icon(
                    painter = painterResource(Res.drawable.ic_square_top_down_linear),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    )
}

@Preview(widthDp = 432)
@Composable
private fun Preview_DetailsTopBar_Comments() {
    Preview_DetailsTopBar(DetailsScreenTab.Comments)
}

@Preview(widthDp = 432)
@Composable
private fun Preview_DetailsTopBar_Web() {
    Preview_DetailsTopBar(DetailsScreenTab.Webview)
}

@Preview(widthDp = 432)
@Composable
private fun Preview_DetailsTopBar_LongName() {
    Preview_DetailsTopBar(
        initialTab = DetailsScreenTab.Webview,
        urlString = "http://rfd.shared.oxide.computer.longname.com"
    )
}

@Composable
private fun Preview_DetailsTopBar(
    initialTab: DetailsScreenTab,
    urlString: String = "https://www.example.com"
) {
    var selectedTab by remember { mutableStateOf(initialTab) }
    AppPreview {
        DetailsTopBar(
            selectedTab = selectedTab,
            urlString = urlString,
            commentCount = 10,
            onTabSelected = {selectedTab = it},
            onBack = {},
            onClickOpenExternal = {},
            modifier = Modifier.border(1.dp, Color.Black)
        )
    }
}