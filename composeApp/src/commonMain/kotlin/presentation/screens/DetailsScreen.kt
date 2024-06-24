@file:OptIn(ExperimentalMaterial3Api::class)

package presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
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
import domain.models.getCommentCount
import domain.models.getCommentIds
import domain.models.getFormatedTime
import domain.models.getPoint
import domain.models.getText
import domain.models.getTitle
import domain.models.getUrl
import domain.models.getUserName
import hackernewskmp.composeapp.generated.resources.Res
import hackernewskmp.composeapp.generated.resources.arrow_back
import hackernewskmp.composeapp.generated.resources.link
import hackernewskmp.composeapp.generated.resources.message
import hackernewskmp.composeapp.generated.resources.user_circle
import io.github.aakira.napier.Napier
import io.ktor.http.Url
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import presentation.viewmodels.DetailsViewModel

class DetailsScreen(private val itemJson: String) : Screen {
    @Composable
    override fun Content() {
        val json = koinInject<Json>()
        val item = Item.from(json, itemJson) ?: throw IllegalStateException("Item is null")
        Scaffold(topBar = { DetailsTopBar() }) { padding ->
            CommentList(item, padding, getScreenModel<DetailsViewModel>())
        }
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
fun CommentList(item: Item, paddingValues: PaddingValues, viewModel: DetailsViewModel = koinInject()) {
    val comments by viewModel.comments
    val listState = rememberLazyListState()
    val isLoading by viewModel.isLoading

    if (isLoading.not()) {
        viewModel.loadComments(item.getCommentIds())
    }

    LazyColumn(
        state = listState,
        modifier = Modifier.padding(
            top = paddingValues.calculateTopPadding(),
            bottom = paddingValues.calculateBottomPadding()
        )
    ) {
        item { ContentWidget(item) }
        items(comments.size) { index ->
            CommentWidget(comments[index])
        }
        if (comments.size < (item.getCommentCount() ?: 0)) {
            item { ItemLoadingWidget() }
        }
    }
}

@Composable
fun ContentWidget(item: Item) {
    val richTextState = rememberRichTextState()
    richTextState.config.apply {
        linkColor = MaterialTheme.colorScheme.tertiary
        codeSpanStrokeColor = Color.Transparent
        codeSpanBackgroundColor = MaterialTheme.colorScheme.tertiaryContainer
        codeSpanColor = MaterialTheme.colorScheme.onTertiaryContainer
    }
    richTextState.setHtml(item.getText() ?: "No content")

    Column(Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = item.getTitle(), Modifier.padding(vertical = 8.dp),
            fontSize = MaterialTheme.typography.titleLarge.fontSize,
            fontFamily = MaterialTheme.typography.titleLarge.fontFamily,
            lineHeight = 28.sp
        )
        item.getUrl()?.let { url ->
            Row {
                Icon(
                    painter = painterResource(Res.drawable.link),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Text(
                    Url(url).host,
                    Modifier.padding(horizontal = 4.dp),
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                )
            }
        }

        Row(modifier = Modifier.padding(vertical = 8.dp)) {
            Card {
                Text(
                    text = "${item.getPoint()} points",
                    fontSize = MaterialTheme.typography.bodySmall.fontSize,
                    modifier = Modifier.padding(horizontal = 8.dp),
                )
            }
            Card(
                modifier = Modifier.padding(start = 8.dp),
            ) {
                Row {
                    Icon(
                        painter = painterResource(Res.drawable.message),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(start = 8.dp).align(Alignment.CenterVertically)
                    )
                    Text(
                        text = "${item.getCommentCount()}",
                        fontSize = MaterialTheme.typography.bodySmall.fontSize,
                        modifier = Modifier.padding(start = 4.dp, end = 8.dp),
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
                fontSize = MaterialTheme.typography.bodyMedium.fontSize
            )
        }
        RichText(
            state = richTextState,
            fontFamily = MaterialTheme.typography.bodyLarge.fontFamily,
            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
        )
        Text(
            text = item.getFormatedTime(),
            modifier = Modifier.padding(bottom = 12.dp),
            fontSize = MaterialTheme.typography.bodyMedium.fontSize
        )
        HorizontalDivider()
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