package presentation.widgets

import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import hackernewskmp.composeapp.generated.resources.Res
import hackernewskmp.composeapp.generated.resources.ic_chat_line_linear
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import ui.AppPreview
import ui.trimmedTextStyle


@Composable
fun LabelledIcon(
    label: String,
    icon: Painter? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = spacedBy(4.dp)
    ) {
        if (icon != null) {
            Icon(
                painter = icon,
                contentDescription = null,
                modifier = Modifier
                    .size(16.dp),
            )
        }
        Text(
            text = label,
            fontSize = MaterialTheme.typography.bodySmall.fontSize,
            style = trimmedTextStyle,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun LabelledIcon(
    label: String,
    placeholder: Painter? = null,
    fallback: Painter? = placeholder,
    url: String? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = spacedBy(4.dp)
    ) {
        if (url != null) {
            AsyncImage(
                model = url,
                contentDescription = null,
                placeholder = placeholder,
                fallback = fallback,
                error = fallback,
                modifier = Modifier
                    .size(16.dp),
            )
        }
        Text(
            text = label,
            fontSize = MaterialTheme.typography.bodySmall.fontSize,
            style = trimmedTextStyle,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview
@Composable
fun Preview_LabelledIcon() {
    AppPreview {
        Column(verticalArrangement = spacedBy(8.dp)) {
            LabelledIcon(
                label = "Sample Label",
                icon = painterResource(Res.drawable.ic_chat_line_linear)
            )
            LabelledIcon(
                label = "Favicon",
                url = "https://www.google.com/s2/favicons?domain=github.com&sz=128",
                placeholder = painterResource(Res.drawable.ic_chat_line_linear)
            )
        }
    }
}