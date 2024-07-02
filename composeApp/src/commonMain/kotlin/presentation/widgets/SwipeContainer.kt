package presentation.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt

@Composable
fun SwipeContainer(
    onSwipeToDismiss: () -> Unit = {},
    swipeThreshold: Float = 300f,
    sensitivityFactor: Float = 1.5f,
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    content: @Composable () -> Unit
) {
    var offset by remember { mutableStateOf(0f) }
    var dismiss by remember { mutableStateOf(false) }
    val density = LocalDensity.current.density

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Box(
            modifier = Modifier
                .offset { IntOffset(offset.roundToInt(), 0) }
                .graphicsLayer(alpha = if (dismiss) 0f else 1 - (offset / swipeThreshold).coerceIn(0f, 1f))
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(onDragEnd = {
                        if (offset > swipeThreshold) {
                            dismiss = true
                            onSwipeToDismiss.invoke()
                        } else {
                            offset = 0f
                        }
                    }) { change, dragAmount ->
                        if (!dismiss) {
                            val newOffset = offset + (dragAmount / density) * sensitivityFactor
                            if (newOffset >= 0) {
                                offset = newOffset
                                if (change.positionChange() != Offset.Zero) change.consume()
                            }
                        }
                    }
                }
        ) {
            content()
        }
    }
}