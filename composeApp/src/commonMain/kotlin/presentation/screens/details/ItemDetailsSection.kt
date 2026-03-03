package presentation.screens.details

import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import be.digitalia.compose.htmlconverter.htmlToAnnotatedString
import domain.models.Item
import domain.models.Poll
import domain.models.PollOption
import domain.models.getFormattedDiffTimeShort
import domain.models.getPoint
import domain.models.getText
import domain.models.getTitle
import domain.models.getUserName
import hackernewskmp.composeapp.generated.resources.Res
import hackernewskmp.composeapp.generated.resources.ic_clock_circle_linear
import hackernewskmp.composeapp.generated.resources.ic_like_outline
import hackernewskmp.composeapp.generated.resources.ic_user_circle_linear
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import ui.trimmedTextStyle


@Composable
fun ItemDetailsSection(
    item: Item,
    pollOptions: ImmutableList<PollOption>
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
                icon = Res.drawable.ic_like_outline
            )
            HeaderChip(
                label = item.getFormattedDiffTimeShort(),
                icon = Res.drawable.ic_clock_circle_linear
            )
            HeaderChip(
                label = item.getUserName(),
                icon = Res.drawable.ic_user_circle_linear
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
    icon: DrawableResource? = null,
) {
    AssistChip(
        modifier = modifier,
        onClick = { },
        label = { Text(label) },
        leadingIcon = icon?.let{ { Icon(painterResource(icon), contentDescription = null, modifier = Modifier.size(16.dp)) } }
    )
}

@Composable
private fun PollContent(
    pollOptions: ImmutableList<PollOption>,
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
