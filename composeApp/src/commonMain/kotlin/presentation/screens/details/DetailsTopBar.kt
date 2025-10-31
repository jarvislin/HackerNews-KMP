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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import extensions.toUrl
import extensions.trimmedHostName
import hackernewskmp.composeapp.generated.resources.Res
import hackernewskmp.composeapp.generated.resources.back
import hackernewskmp.composeapp.generated.resources.ic_arrow_left_linear
import hackernewskmp.composeapp.generated.resources.ic_chat_line_linear
import hackernewskmp.composeapp.generated.resources.ic_global_outline
import hackernewskmp.composeapp.generated.resources.ic_square_top_down_linear
import hackernewskmp.composeapp.generated.resources.x_comments
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import presentation.widgets.SquircleBadge
import ui.AppPreview

private const val INDEX_COMMENTS = 0
private const val INDEX_WEB = 1

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsTopBar(
    selectedTabIndex: Int,
    urlString: String?,
    commentCount: Int,
    onTabSelected: (Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    onClickLink: (() -> Unit)? = null
) {
    val trimmedHostName = urlString?.toUrl()?.trimmedHostName()
    val commentsLabel = pluralStringResource(Res.plurals.x_comments, commentCount, commentCount)
    TopAppBar(
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors().run { copy(containerColor = containerColor.copy(alpha = 0.9f)) },
        title = {
            if (selectedTabIndex == INDEX_COMMENTS || trimmedHostName == null) {
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
                    contentDescription = stringResource(Res.string.back),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        actions = {
            if (selectedTabIndex == INDEX_COMMENTS && trimmedHostName != null) {
                IconButton(onClick = {onTabSelected(INDEX_WEB)}) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_global_outline),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            if (selectedTabIndex == INDEX_WEB) {
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
                    IconButton(onClick = {onTabSelected(INDEX_COMMENTS)}) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_chat_line_linear),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
            if (urlString != null && onClickLink != null) {
                IconButton(onClick = onClickLink) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_square_top_down_linear),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    )
}

@Preview(widthDp = 432)
@Composable
private fun Preview_DetailsTopBar_Comments() {
    Preview_DetailsTopBar(INDEX_COMMENTS)
}

@Preview(widthDp = 432)
@Composable
private fun Preview_DetailsTopBar_Web() {
    Preview_DetailsTopBar(INDEX_WEB)
}

@Preview(widthDp = 432)
@Composable
private fun Preview_DetailsTopBar_LongName() {
    Preview_DetailsTopBar(
        initialTab = INDEX_WEB,
        urlString = "http://rfd.shared.oxide.computer.longname.com"
    )
}

@Composable
private fun Preview_DetailsTopBar(
    initialTab: Int,
    urlString: String = "https://www.example.com"
) {
    var selectedTab by remember { mutableIntStateOf(initialTab) }
    AppPreview {
        DetailsTopBar(
            selectedTabIndex = selectedTab,
            urlString = urlString,
            commentCount = 10,
            onTabSelected = {selectedTab = it},
            onBack = {},
            onClickLink = {},
            modifier = Modifier.border(1.dp, Color.Black)
        )
    }
}