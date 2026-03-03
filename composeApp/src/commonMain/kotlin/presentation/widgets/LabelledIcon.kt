package presentation.widgets

import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import hackernewskmp.composeapp.generated.resources.Res
import hackernewskmp.composeapp.generated.resources.ic_chat_line_linear
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import ui.AppPreview
import ui.trimmedTextStyle


@Composable
fun LabelledIcon(
    label: String,
    icon: DrawableResource? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = spacedBy(4.dp)
    ) {
        if (icon != null) {
            Icon(
                painter = painterResource(icon),
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
    placeholder: DrawableResource? = null,
    url: String? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = spacedBy(4.dp)
    ) {
        if (url != null) {
            Box(
                modifier = Modifier
                    .size(16.dp)
            ) {
                var showPlaceholder by remember { mutableStateOf(true) }
                if (placeholder != null && showPlaceholder) {
                    Icon(
                        painter = painterResource(placeholder),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
                AsyncImage(
                    model = url,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    onState = { state ->
                        showPlaceholder = when (state) {
                            is AsyncImagePainter.State.Success -> false
                            else -> true
                        }
                    }
                )
            }
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
                icon = Res.drawable.ic_chat_line_linear
            )
            LabelledIcon(
                label = "Favicon",
                url = "https://www.google.com/s2/favicons?domain=github.com&sz=128",
                placeholder = Res.drawable.ic_chat_line_linear
            )
        }
    }
}