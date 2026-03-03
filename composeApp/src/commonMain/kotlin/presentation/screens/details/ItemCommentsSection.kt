package presentation.screens.details

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import be.digitalia.compose.htmlconverter.htmlToAnnotatedString
import domain.models.Comment
import domain.models.Item
import domain.models.getFormattedDiffTimeShort
import domain.models.getText
import domain.models.getUserName
import domain.models.sampleCommentsJson
import hackernewskmp.composeapp.generated.resources.Res
import hackernewskmp.composeapp.generated.resources.ic_user_circle_linear
import hackernewskmp.composeapp.generated.resources.no_comment
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import presentation.widgets.IndentedBox
import presentation.widgets.SquircleBadge
import ui.AppPreview
import kotlin.time.ExperimentalTime


fun LazyListScope.commentItem(
    commentId: Long,
    depth: Int,
    getComment: (Long) -> Comment?,
    isCollapsed: (Long) -> Boolean,
    countDescendants: (Long) -> Int,
    onToggleCollapse: (Long) -> Unit
) {
    val comment = getComment(commentId) ?: return
    val isCollapsedValue = isCollapsed(commentId)
    val descendantsCount = if (isCollapsedValue) countDescendants(commentId) else null

    item(key = "comment-$commentId") {
        CommentRow(
            comment = comment,
            depth = depth,
            descendantsCount = descendantsCount,
            onToggleCollapse = { onToggleCollapse(commentId) },
            modifier = Modifier.animateItem()
        )
    }

    if (!isCollapsedValue) {
        comment.commentIds.forEach { replyCommentId ->
            commentItem(
                commentId = replyCommentId,
                depth = depth + 1,
                getComment = getComment,
                isCollapsed = isCollapsed,
                countDescendants = countDescendants,
                onToggleCollapse = onToggleCollapse,
            )
        }
    }
}

@Composable
fun CommentRow(
    comment: Comment,
    depth: Int,
    onToggleCollapse: () -> Unit,
    modifier: Modifier = Modifier,
    descendantsCount: Int? = null,
) {
    val html = comment.getText() ?: stringResource(Res.string.no_comment)
    val username = comment.getUserName()
    val since = comment.getFormattedDiffTimeShort()
    val annotated = remember(html) { htmlToAnnotatedString(html) }

    IndentedBox(
        modifier = modifier
            .clickable(enabled = comment.commentIds.isNotEmpty(), onClick = onToggleCollapse),
        depth = depth
    ) {
        Column {
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
                        text = username,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = since,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    SquircleBadge(
                        modifier = Modifier
                            .alpha(if (descendantsCount != null) 1f else 0f)
                    ) {
                        Text("+$descendantsCount")
                    }
                }
            }
            Text(
                modifier = Modifier.padding(top = 12.dp),
                text = annotated,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Preview
@Composable
private fun Preview_HtmlAnnotatedString() {
    val annotated = remember(sampleHtmlAnnotatedComment) { htmlToAnnotatedString(sampleHtmlAnnotatedComment) }
    AppPreview {
        Text(
            text = annotated,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@OptIn(ExperimentalTime::class)
@Preview
@Composable
private fun Preview_CommentWidget() {
    AppPreview {
        Column {
            sampleCommentsJson
                .mapNotNull { Item.from(Json, it) }
                .forEach {
                    CommentRow(
                        comment = it as Comment,
                        depth = 0,
                        onToggleCollapse = {},
                        modifier = Modifier,
                        descendantsCount = 5,
                    )
                }
        }
    }
}

private val sampleHtmlAnnotatedComment = """
<p>This is a sample comment text to demonstrate the styling.</p><p>This is another paragraph with <strong>bold</strong> and <em>italic</em> text.</p>    
""".trimIndent()