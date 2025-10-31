package presentation.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BadgeDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import sv.lib.squircleshape.SquircleShape

/**
 * Like Material3 Badge, but using the SquircleShape.
 */
@Composable
fun SquircleBadge(
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = contentColorFor(containerColor),
    content: @Composable (RowScope.() -> Unit)? = null,
) {
    val size = 16.dp
    val shape = SquircleShape(8.dp)

    // Draw badge container.
    Row(
        modifier =
            modifier
                .defaultMinSize(minWidth = size, minHeight = size)
                .background(color = containerColor, shape = shape)
                .then(
                    if (content != null)
                        Modifier.padding(horizontal = 4.dp)
                    else Modifier
                ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        if (content != null) {
            // Not using Surface composable because it blocks touch propagation behind it.
            val mergedStyle = LocalTextStyle.current.merge(MaterialTheme.typography.labelSmall)
            CompositionLocalProvider(
                LocalContentColor provides contentColor,
                LocalTextStyle provides mergedStyle,
                content = { content() },
            )
        }
    }
}