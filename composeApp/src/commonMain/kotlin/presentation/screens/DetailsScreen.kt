@file:OptIn(ExperimentalMaterial3Api::class)

package presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichText
import domain.models.Comment
import domain.models.Item
import domain.models.Poll
import domain.models.PollOption
import domain.models.getCommentCount
import domain.models.getCommentIds
import domain.models.getFormatedTime
import domain.models.getPoint
import domain.models.getText
import domain.models.getTitle
import domain.models.getUrl
import domain.models.getUserName
import getPlatform
import hackernewskmp.composeapp.generated.resources.Res
import hackernewskmp.composeapp.generated.resources.arrow_back
import hackernewskmp.composeapp.generated.resources.link
import hackernewskmp.composeapp.generated.resources.message
import hackernewskmp.composeapp.generated.resources.user_circle
import io.ktor.http.Url
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import presentation.viewmodels.DetailsViewModel
import presentation.widgets.SwipeContainer
import ui.trimmedTextStyle

class DetailsScreen(private val itemJson: String) : Screen {
    @Composable
    override fun Content() {
        val viewModel = getScreenModel<DetailsViewModel>()
        val error by viewModel.error
        val snackBarHostState = remember { SnackbarHostState() }
        val json = koinInject<Json>()
        val item = Item.from(json, itemJson) ?: throw IllegalStateException("Item is null")
        val navigator = LocalNavigator.currentOrThrow

        if (getPlatform().isAndroid()) {
            ScaffoldContent(snackBarHostState, viewModel, item)
        } else {
            SwipeContainer(
                onSwipeToDismiss = { navigator.pop() },
                swipeThreshold = getPlatform().getScreenWidth() / 3.5f,
            ) {
                ScaffoldContent(snackBarHostState, viewModel, item)
            }
        }

        error?.let {
            LaunchedEffect(Unit) {
                val result = snackBarHostState.showSnackbar(it.message ?: "An error occurred", "Retry")
                if (result == SnackbarResult.ActionPerformed) {
                    viewModel.reset()
                }
            }
        }
    }

    @Composable
    fun ScaffoldContent(snackBarHostState: SnackbarHostState, viewModel: DetailsViewModel, item: Item) {
        Scaffold(
            topBar = { DetailsTopBar() },
            snackbarHost = { SnackbarHost(snackBarHostState) }
        ) { padding ->
            CommentList(item, padding, viewModel)
        }
    }

    @Composable
    fun DetailsTopBar() {
        val navigator = LocalNavigator.currentOrThrow
        TopAppBar(
            title = {
                Text(
                    text = "Details",
                    fontFamily = MaterialTheme.typography.bodyLarge.fontFamily
                )
            },
            navigationIcon = {
                IconButton(onClick = { navigator.pop() }) {
                    Icon(
                        painter = painterResource(Res.drawable.arrow_back),
                        contentDescription = "Back",
                    )
                }
            }
        )
    }

    @Composable
    fun CommentList(item: Item, paddingValues: PaddingValues, viewModel: DetailsViewModel) {
        val comments by viewModel.comments
        val pollOptions by viewModel.pollOptions
        val listState = rememberLazyListState()
        val isLoadingPollOptions by viewModel.isLoadingPollOptions
        val isLoadingComments by viewModel.isLoadingComments
        val error by viewModel.error

        if (item is Poll && isLoadingPollOptions.not() && error == null && pollOptions.isEmpty()) {
            viewModel.loadPollOptions(item.optionIds)
        }

        if (isLoadingComments.not() && error == null && item.getCommentIds().isNotEmpty()) {
            viewModel.loadComments(item.getCommentIds())
        }

        LazyColumn(
            state = listState,
            modifier = Modifier.padding(
                top = paddingValues.calculateTopPadding(),
                bottom = paddingValues.calculateBottomPadding()
            )
        ) {
            item { ContentWidget(item, pollOptions) }
            items(comments.size) { index ->
                CommentWidget(comments[index])
            }
            if (comments.size < item.getCommentIds().size) item { ItemLoadingWidget() }
        }
    }

    @Composable
    fun ContentWidget(item: Item, pollOptions: List<PollOption>) {
        val richTextState = rememberRichTextState()
        richTextState.config.apply {
            linkColor = MaterialTheme.colorScheme.tertiary
            codeSpanStrokeColor = Color.Transparent
            codeSpanBackgroundColor = MaterialTheme.colorScheme.tertiaryContainer
            codeSpanColor = MaterialTheme.colorScheme.onTertiaryContainer
        }

        Column(Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = item.getTitle(), Modifier.padding(vertical = 8.dp),
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                fontFamily = MaterialTheme.typography.titleLarge.fontFamily,
                lineHeight = 28.sp
            )
            Row(modifier = Modifier.padding(bottom = 8.dp)) {
                Card {
                    Text(
                        text = "${item.getPoint()} points",
                        fontSize = MaterialTheme.typography.bodySmall.fontSize,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = trimmedTextStyle
                    )
                }
                Card(
                    modifier = Modifier.padding(start = 8.dp),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.message),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = "${item.getCommentCount()}",
                            fontSize = MaterialTheme.typography.bodySmall.fontSize,
                            modifier = Modifier.padding(start = 4.dp),
                            style = trimmedTextStyle
                        )
                    }
                }
            }
            Row(modifier = Modifier.padding(top = 8.dp)) {
                Icon(
                    painter = painterResource(Res.drawable.user_circle),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Text(
                    modifier = Modifier.padding(start = 4.dp),
                    text = item.getUserName(),
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    style = trimmedTextStyle
                )
            }
            if (item is Poll) {
                Column {
                    pollOptions.forEachIndexed { index: Int, option: PollOption ->
                        PollOptionWidget(option, pollOptions.size, index)
                    }
                }
            }
            item.getText()?.let { text ->
                richTextState.setHtml(text)
                RichText(
                    state = richTextState,
                    fontFamily = MaterialTheme.typography.bodyLarge.fontFamily,
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                )
            }
            item.getUrl()?.let { LinkWidget(item) }
            Text(
                text = item.getFormatedTime(),
                modifier = Modifier.padding(bottom = 12.dp).padding(top = 8.dp),
                fontSize = MaterialTheme.typography.bodyMedium.fontSize
            )
            HorizontalDivider()
        }
    }

    @Composable
    fun PollOptionWidget(option: PollOption, size: Int, index: Int) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Card(colors = CardDefaults.cardColors(MaterialTheme.colorScheme.tertiaryContainer)) {
                Box(
                    modifier = Modifier.padding(horizontal = 8.dp).defaultMinSize(minWidth = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${option.score}",
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        fontSize = MaterialTheme.typography.bodySmall.fontSize,
                    )
                }
            }
            Text(
                text = option.text,
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                modifier = Modifier.padding(start = 10.dp)
            )
        }
        if (index < size - 1) {
            Spacer(modifier = Modifier.height(12.dp))
        }
    }

    @Composable
    fun LinkWidget(item: Item) {
        val navigator = LocalNavigator.currentOrThrow
        val json = koinInject<Json>()

        Spacer(modifier = Modifier.height(8.dp))
        Card(
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondaryContainer),
            modifier = Modifier.height(40.dp),
            shape = RoundedCornerShape(20.dp),
            onClick = { navigator.push(WebScreen(item.toJson(json))) }) {
            Row(modifier = Modifier.fillMaxHeight(), verticalAlignment = Alignment.CenterVertically) {
                Spacer(modifier = Modifier.size(16.dp))
                Icon(
                    painter = painterResource(Res.drawable.link),
                    contentDescription = "Link",
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    modifier = Modifier.padding(start = 8.dp, end = 24.dp),
                    fontSize = MaterialTheme.typography.labelLarge.fontSize,
                    fontFamily = MaterialTheme.typography.labelLarge.fontFamily,
                    fontWeight = MaterialTheme.typography.labelLarge.fontWeight,
                    style = trimmedTextStyle,
                    text = Url(item.getUrl()!!).host,
                )
            }
        }
    }

    @Composable
    fun CommentWidget(comment: Comment) {
        val paddingStart = 12.dp * (comment.depth + 1)
        val localUriHandler = LocalUriHandler.current
        val richTextState = rememberRichTextState()
        richTextState.config.apply {
            linkColor = MaterialTheme.colorScheme.tertiary
            codeSpanStrokeColor = Color.Transparent
            codeSpanBackgroundColor = MaterialTheme.colorScheme.tertiaryContainer
            codeSpanColor = MaterialTheme.colorScheme.onTertiaryContainer
        }
        richTextState.setHtml(comment.getText() ?: "No content")

        val uriHandler by remember {
            mutableStateOf(object : UriHandler {
                override fun openUri(uri: String) {
                    localUriHandler.openUri(decodeUrl(uri))
                }
            })
        }
        Column(Modifier.padding(horizontal = 16.dp).padding(start = paddingStart, top = 12.dp)) {
            Row {
                Icon(
                    painter = painterResource(Res.drawable.user_circle),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Text(
                    text = comment.getUserName(),
                    modifier = Modifier.padding(start = 4.dp),
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    style = trimmedTextStyle
                )
            }
            CompositionLocalProvider(LocalUriHandler provides uriHandler) {
                RichText(
                    modifier = Modifier.padding(vertical = 12.dp),
                    state = richTextState,
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                )
            }
            Text(
                text = comment.getFormatedTime(),
                modifier = Modifier.padding(bottom = 12.dp),
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
            )
            HorizontalDivider()
        }
    }

    private fun decodeUrl(url: String): String {
        val entityPattern = Regex("&#x([0-9A-Fa-f]+);")
        return url.replace(entityPattern) { matchResult ->
            val codePoint = matchResult.groupValues[1].toInt(16)
            CharArray(1) { codePoint.toChar() }.concatToString()
        }
    }
}