package presentation.widgets

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ui.AppPreview

@Composable
private fun commentDepthColor(depth: Int): Color {
    return when (depth % 4) {
        0 -> MaterialTheme.colorScheme.primaryContainer
        1 -> MaterialTheme.colorScheme.secondaryContainer
        2 -> MaterialTheme.colorScheme.inversePrimary
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
}

@Composable
fun IndentedBox(
    depth: Int,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val paddingStart = 12.dp * (depth + 1)
    val color = commentDepthColor(depth)
    Box(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .padding(start = (paddingStart - 12.dp), top = 6.dp, bottom = 6.dp)
            .drawWithContent {
                drawLine(
                    color = color,
                    start = Offset.Zero,
                    end = Offset(0f, size.height),
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round
                )
                drawContent()
            }
            .padding(start = 12.dp),
        content = content
    )
}

@Preview
@Composable
private fun Preview_CommentWidget() {
    AppPreview {
        Column {
            listOf(0, 1, 2, 3, 4, 5, 3, 3).forEach {
                IndentedBox(depth = it) {
                    Text("Indented $it")
                }
            }
        }
    }
}
