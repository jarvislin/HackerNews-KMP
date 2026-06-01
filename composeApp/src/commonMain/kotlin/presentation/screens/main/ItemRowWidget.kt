package presentation.screens.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import domain.models.Item
import domain.models.getCommentCount
import domain.models.getFormattedDiffTimeShort
import domain.models.getPoint
import domain.models.getTitle
import domain.models.getUrl
import domain.models.sampleAskJson
import domain.models.sampleJobJson
import domain.models.samplePollJson
import domain.models.sampleStoryJson
import extensions.faviconUrl
import extensions.trimmedHostName
import hackernewskmp.composeapp.generated.resources.Res
import hackernewskmp.composeapp.generated.resources.ic_chat_line_linear
import hackernewskmp.composeapp.generated.resources.ic_clock_circle_linear
import hackernewskmp.composeapp.generated.resources.ic_like_outline
import hackernewskmp.composeapp.generated.resources.ic_link_minimalistic_linear
import io.ktor.http.Url
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.painterResource
import presentation.widgets.LabelledIcon
import ui.AppPreview
import kotlin.time.ExperimentalTime


@Composable
fun ItemRowWidget(
    item: Item,
    seen: Boolean,
    onClickItem: () -> Unit,
    onClickComment: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .alpha(if (seen) 0.4f else 1f)
    ) {
        Column(
            verticalArrangement = spacedBy(8.dp),
            modifier = Modifier
                .weight(1f)
                .clickable(onClick = onClickItem)
                .padding(8.dp)
        ) {
            Text(
                text = item.getTitle(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = spacedBy(8.dp)
            ) {
                item.getUrl()?.let { urlString ->
                    val url = Url(urlString)
                    LabelledIcon(
                        label = url.trimmedHostName(),
                        url = url.faviconUrl(),
                        placeholder = Res.drawable.ic_link_minimalistic_linear,
                    )
                }
                LabelledIcon(
                    label = item.getPoint().toString(),
                    icon = Res.drawable.ic_like_outline,
                )
                LabelledIcon(
                    label = item.getFormattedDiffTimeShort(),
                    icon = Res.drawable.ic_clock_circle_linear,
                )
            }
        }
        item.getCommentCount()?.let { commentCount ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable(onClick = onClickComment)
                    .padding(top = 8.dp, bottom = 8.dp)
                    .minimumInteractiveComponentSize()
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_chat_line_linear),
                    contentDescription = null,
                )
                Text(
                    text = "$commentCount",
                    style = MaterialTheme.typography.labelLarge,
                )

            }
        }
    }
}

@Preview
@Composable
private fun Preview_ItemRowWidget_Dark() {
    Preview_ItemRowWidget(darkTheme = true)
}

@Preview
@Composable
private fun Preview_ItemRowWidget_Light() {
    Preview_ItemRowWidget(darkTheme = false)
}

@Composable
private fun Preview_ItemRowWidget(darkTheme: Boolean) {
    AppPreview(darkTheme = darkTheme) {
        Column {
            previewItems.forEachIndexed { index, item ->
                ItemRowWidget(
                    item = item,
                    seen = index % 2 == 0,
                    onClickItem = {},
                    onClickComment = {},
                )
            }
        }
    }
}

@OptIn(ExperimentalTime::class)
val previewItems: List<Item> =
    listOf(
        sampleStoryJson,
        sampleAskJson,
        sampleJobJson,
        samplePollJson,
    )
        .map { Item.from(Json, it)!! }