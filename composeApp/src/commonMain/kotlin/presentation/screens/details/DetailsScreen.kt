@file:OptIn(ExperimentalMaterial3Api::class)

package presentation.screens.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import be.digitalia.compose.htmlconverter.htmlToAnnotatedString
import domain.models.Comment
import domain.models.Item
import domain.models.Poll
import domain.models.PollOption
import domain.models.getCommentCount
import domain.models.getCommentIds
import domain.models.getFormattedDiffTimeShort
import domain.models.getPoint
import domain.models.getText
import domain.models.getTitle
import domain.models.getUrl
import domain.models.getUserName
import hackernewskmp.composeapp.generated.resources.Res
import hackernewskmp.composeapp.generated.resources.an_error_occurred
import hackernewskmp.composeapp.generated.resources.back
import hackernewskmp.composeapp.generated.resources.ic_arrow_left_linear
import hackernewskmp.composeapp.generated.resources.ic_chat_line_linear
import hackernewskmp.composeapp.generated.resources.ic_clock_circle_linear
import hackernewskmp.composeapp.generated.resources.ic_like_outline
import hackernewskmp.composeapp.generated.resources.ic_link_minimalistic_linear
import hackernewskmp.composeapp.generated.resources.ic_user_circle_linear
import hackernewskmp.composeapp.generated.resources.no_comment
import hackernewskmp.composeapp.generated.resources.retry
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject
import presentation.screens.main.ItemLoadingWidget
import presentation.viewmodels.DetailsViewModel
import presentation.viewmodels.MainViewModel
import ui.trimmedTextStyle
import utils.Constants
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours
import kotlin.time.ExperimentalTime

@Serializable
data class DetailsRoute(
    @SerialName("id")
    val id: Long
)

@Composable
fun DetailsScreen(
    itemId: Long,
    onBack: () -> Unit,
    onClickItem: (Item) -> Unit
) {
    val viewModel = koinInject<DetailsViewModel>()
    val mainViewModel = koinInject<MainViewModel>()
    val state by viewModel.state
    val snackBarHostState = remember { SnackbarHostState() }
    val item = mainViewModel.state.value.items.first { it.getItemId() == itemId }
    val onClickLink = { onClickItem(item) }

    ScaffoldContent(snackBarHostState, viewModel, item, onBack, onClickLink)

    LaunchedEffect(Unit) {
        if (state.error != null) {
            val result = snackBarHostState.showSnackbar(
                message = state.error?.message ?: getString(Res.string.an_error_occurred),
                actionLabel = getString(Res.string.retry)
            )
            if (result == SnackbarResult.ActionPerformed) {
                viewModel.reset()
            }
        }
    }
}

@Composable
fun ScaffoldContent(
    snackBarHostState: SnackbarHostState,
    viewModel: DetailsViewModel,
    item: Item,
    onBack: () -> Unit,
    onClickLink: () -> Unit
) {
    Scaffold(
        topBar = {
            DetailsTopBar(
                onBack = onBack,
                onClickLink = onClickLink.takeIf { item.getUrl() != null }
            )
        },
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { padding ->
        CommentList(item, padding, viewModel)
    }
}

@Composable
fun DetailsTopBar(
    onBack: () -> Unit,
    onClickLink: (() -> Unit)? = null
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors().run { copy(containerColor = containerColor.copy(alpha = 0.9f)) },
        title = { },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    painter = painterResource(Res.drawable.ic_arrow_left_linear),
                    contentDescription = stringResource(Res.string.back)
                )
            }
        },
        actions = {
            if (onClickLink != null) {
                IconButton(onClick = onClickLink) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_link_minimalistic_linear),
                        contentDescription = null
                    )
                }
            }
        }
    )
}

@Composable
fun CommentList(
    item: Item,
    contentPadding: PaddingValues,
    viewModel: DetailsViewModel,
) {
    val listState = rememberLazyListState()
    val state by viewModel.state
    if (item is Poll && state.loadingPollOptions.not() && state.error == null && state.pollOptions.isEmpty()) {
        viewModel.loadPollOptions(item.optionIds)
    }

    if (state.loadingComments.not() && state.error == null && item.getCommentIds().isNotEmpty()) {
        viewModel.loadComments(item.getCommentIds())
    }

    LazyColumn(
        state = listState,
        contentPadding = contentPadding,
    ) {
        item { ContentWidget(item, state.pollOptions) }
        itemsIndexed(items = state.comments, key = { _, comment -> comment.id }) { _, comment ->
            CommentWidget(comment)
        }
        if (state.comments.size < item.getCommentIds().size) item { ItemLoadingWidget() }
    }
}

@Composable
fun ContentWidget(
    item: Item,
    pollOptions: List<PollOption>
) {
    Column(Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = item.getTitle(),
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            horizontalArrangement = spacedBy(8.dp)
        ) {
            HeaderChip(
                label = item.getPoint().toString(),
                icon = painterResource(Res.drawable.ic_like_outline)
            )
            item.getCommentCount()?.let {
                HeaderChip(
                    label = it.toString(),
                    icon = painterResource(Res.drawable.ic_chat_line_linear)
                )
            }
            HeaderChip(
                label = item.getFormattedDiffTimeShort(),
                icon = painterResource(Res.drawable.ic_clock_circle_linear)
            )
            HeaderChip(
                label = item.getUserName(),
                icon = painterResource(Res.drawable.ic_user_circle_linear)
            )
        }
        if (item is Poll) {
            PollContent(pollOptions)
        }
        item.getText()?.let { text ->
            val annotated = remember(text) { htmlToAnnotatedString(text) }
            Text(
                text = annotated,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
            )
        }
    }
}

@Composable
private fun HeaderChip(
    label: String,
    modifier: Modifier = Modifier,
    icon: Painter? = null,
) {
    AssistChip(
        modifier = modifier,
        onClick = { },
        label = { Text(label) },
        leadingIcon = icon?.let{ { Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp)) } }
    )
}

@Composable
private fun PollContent(
    pollOptions: List<PollOption>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Spacer(modifier = Modifier.height(8.dp))
        pollOptions.forEachIndexed { index: Int, option: PollOption ->
            PollOptionWidget(option, pollOptions.size, index)
        }
    }
}

@Composable
fun PollOptionWidget(option: PollOption, size: Int, index: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Card(colors = CardDefaults.cardColors(MaterialTheme.colorScheme.tertiaryContainer)) {
            Box(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    .defaultMinSize(minWidth = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${option.score}",
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    fontSize = MaterialTheme.typography.bodySmall.fontSize,
                    style = trimmedTextStyle,
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
fun CommentWidget(
    comment: Comment,
    modifier: Modifier = Modifier,
) {
    val paddingStart = 12.dp * (comment.depth + 1)
    val localUriHandler = LocalUriHandler.current
    val html = comment.getText() ?: stringResource(Res.string.no_comment)
    val annotated = remember(html) { htmlToAnnotatedString(html) }

    val uriHandler by remember {
        mutableStateOf(object : UriHandler {
            override fun openUri(uri: String) {
                localUriHandler.openUri(decodeUrl(uri))
            }
        })
    }
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .padding(start = paddingStart, top = 12.dp)
    ) {
        CompositionLocalProvider(
            LocalContentColor provides MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            LocalTextStyle provides MaterialTheme.typography.bodySmall
        ) {
            Row(
                horizontalArrangement = spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_user_circle_linear),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                )
                Text(
                    text = comment.getUserName(),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = comment.getFormattedDiffTimeShort(),
                )
            }
        }
        CompositionLocalProvider(LocalUriHandler provides uriHandler) {
            Text(
                modifier = Modifier.padding(vertical = 12.dp),
                text = annotated,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        HorizontalDivider()
    }
}

fun decodeUrl(url: String): String {
    val entityPattern = Regex(Constants.REGEX_PATTERN)
    return url.replace(entityPattern) { matchResult ->
        val codePoint = matchResult.groupValues[1].toInt(16)
        CharArray(1) { codePoint.toChar() }.concatToString()
    }
}

@OptIn(ExperimentalTime::class)
@Preview
@Composable
fun Preview_CommentWidget() {
    val html = "<p>This is a sample comment text to demonstrate the styling.</p><p>This is another paragraph with <strong>bold</strong> and <em>italic</em> text.</p>"

    val comment = Comment(
        id = 1L,
        userName = "john_doe",
        text = html,
        depth = 2,
        time = (Clock.System.now() - 23.hours).epochSeconds,
        commentIds = emptyList(),
        parentId = 0L,
    )

    MaterialTheme {
        Column(
            verticalArrangement = spacedBy(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            CommentWidget(comment = comment)

            val annotated = remember(html) { htmlToAnnotatedString(html) }
            Text(
                text = annotated,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}
