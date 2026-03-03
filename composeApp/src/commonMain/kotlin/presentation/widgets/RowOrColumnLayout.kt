package presentation.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ui.AppPreview
import kotlin.math.max

/**
 * A layout that arranges its primary and secondary content in a row if they fit,
 * otherwise arranges them in a column.
 *
 * In a row configuration, the primary content is placed at the start and the secondary
 * content is placed at the end.
 *
 * In a column configuration, the primary content is placed at the top and the secondary
 * content is placed at the bottom, both start-aligned.
 */
@Composable
fun RowOrColumnLayout(
    modifier: Modifier = Modifier,
    primary: @Composable () -> Unit,
    secondary: @Composable () -> Unit,
) {
    Layout(
        content = {
            primary()
            secondary()
        },
        modifier = modifier
    ) { measurables, constraints ->
        require(measurables.size == 2) { "RowOrColumnLayout requires exactly two children." }

        val primaryPlaceable = measurables[0].measure(constraints.copy(minWidth = 0))
        val secondaryPlaceable = measurables[1].measure(constraints.copy(minWidth = 0))

        val availableWidth = constraints.maxWidth

        if (primaryPlaceable.width + secondaryPlaceable.width <= availableWidth) {
            // Place in a Row
            val height = max(primaryPlaceable.height, secondaryPlaceable.height)
            layout(availableWidth, height) {
                primaryPlaceable.placeRelative(0, (height - primaryPlaceable.height) / 2)
                secondaryPlaceable.placeRelative(
                    x = availableWidth - secondaryPlaceable.width,
                    y = (height - secondaryPlaceable.height) / 2
                )
            }
        } else {
            // Place in a Column
            val width = max(primaryPlaceable.width, secondaryPlaceable.width)
            val height = primaryPlaceable.height + secondaryPlaceable.height
            layout(width, height) {
                primaryPlaceable.placeRelative(0, 0)
                secondaryPlaceable.placeRelative(0, primaryPlaceable.height)
            }
        }
    }
}

@Preview(name = "Row - Fits", widthDp = 400)
@Preview(name = "Column - Does not fit", widthDp = 250)
@Composable
private fun RowOrColumnLayoutPreview() {
    AppPreview {
        RowOrColumnLayout(
            primary = {
                Text(
                    text = "Primary Content (Longer text)",
                    modifier = Modifier
                        .background(Color.Yellow)
                        .padding(8.dp)
                )
            },
            secondary = {
                Text(
                    text = "Secondary",
                    modifier = Modifier
                        .background(Color.Green)
                        .padding(8.dp)
                )
            }
        )
    }
}
