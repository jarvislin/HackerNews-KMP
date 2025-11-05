package presentation.screens.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.unit.dp
import domain.models.Item
import domain.models.getUrl
import extensions.shareCommentsText
import extensions.shareLinkText
import hackernewskmp.composeapp.generated.resources.Res
import hackernewskmp.composeapp.generated.resources.Share_Comments
import hackernewskmp.composeapp.generated.resources.Share_Link
import hackernewskmp.composeapp.generated.resources.ic_comments_share
import hackernewskmp.composeapp.generated.resources.ic_square_share_line_linear
import hackernewskmp.composeapp.generated.resources.ic_square_top_down_linear
import hackernewskmp.composeapp.generated.resources.open_with_the_default_browser
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import presentation.screens.main.previewItems
import ui.AppPreview
import ui.googleSansCodeFontFamily

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun DetailsShareSheet(
    isVisible: Boolean,
    item: Item,
    onOpenInBrowser: () -> Unit,
    onShareLink: () -> Unit,
    onShareComments: () -> Unit,
    onVisibility: (Boolean) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(isVisible) {
        scope.launch {
            if (isVisible && !bottomSheetState.isVisible) {
                bottomSheetState.expand()
            }
            else if (!isVisible && bottomSheetState.isVisible) {
                bottomSheetState.hide()
            }
        }
    }

    LaunchedEffect(bottomSheetState.isVisible) {
        onVisibility(bottomSheetState.isVisible)
    }

    if (isVisible) {
        ModalBottomSheet(
            sheetState = bottomSheetState,
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            onDismissRequest = {},
            content = {
                SheetContent(
                    item = item,
                    onOpenInBrowser = onOpenInBrowser,
                    onShareLink = onShareLink,
                    onShareComments = onShareComments,
                )
            },
        )

        // Handle Back Gesture / Back Press to collapse sheet
        BackHandler {
            scope.launch {
                bottomSheetState.hide()
            }
        }
    }
}

@Composable
private fun SheetContent(
    item: Item,
    onOpenInBrowser: () -> Unit,
    onShareLink: () -> Unit,
    onShareComments: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
    ) {
        item.getUrl()?.let { urlString ->
            SheetItem(
                icon = Res.drawable.ic_square_top_down_linear,
                buttonText = Res.string.open_with_the_default_browser,
                sharedText = urlString,
                onClick = onOpenInBrowser
            )
            Spacer(modifier = Modifier.height(16.dp))
            SheetItem(
                icon = Res.drawable.ic_square_share_line_linear,
                buttonText = Res.string.Share_Link,
                sharedText = item.shareLinkText(),
                onClick = onShareLink
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        SheetItem(
            icon = Res.drawable.ic_comments_share,
            buttonText = Res.string.Share_Comments,
            sharedText = item.shareCommentsText(),
            onClick = onShareComments
        )
    }
}

@Composable
private fun SheetItem(
    icon: DrawableResource,
    buttonText: StringResource,
    sharedText: String,
    onClick: () -> Unit,
) {
    TextButton(onClick) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(stringResource(buttonText))
    }
    SelectionContainer(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = sharedText,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelSmall,
            fontFamily = googleSansCodeFontFamily(),
        )
    }
}

@Preview
@Composable
private fun Preview_SheetContent() {
    AppPreview {
        SheetContent(
            item = previewItems.first(),
            onShareLink = {},
            onShareComments = {},
            onOpenInBrowser = {},
        )
    }
}