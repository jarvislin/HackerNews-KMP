package domain.models

import extensions.TimeExtension.format
import extensions.TimeExtension.toInstant
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.DurationUnit

/**
 * Base class for all items.
 */
sealed class Item

fun Item.getCommentCount(): Int? = when (this) {
    is Ask -> countOfComment
    is Poll -> countOfComment
    is Story -> countOfComment
    else -> null
}

fun Item.getFormatedTime(): String {
    val instant = when (this) {
        is Ask -> time
        is Job -> time
        is Poll -> time
        is PollOption -> time
        is Story -> time
        is Comment -> time
    }.toInstant()

    return when (val diff = Clock.System.now().minus(instant).toLong(DurationUnit.SECONDS)) {
        in 0..60 -> "$diff seconds ago"
        in 60..3600 -> "${diff / 60} minutes ago"
        in 3600..86400 -> "${diff / 3600} hours ago"
        in 86400..86400 * 3 -> "${diff / 86400} days ago"
        else -> instant.toLocalDateTime(TimeZone.currentSystemDefault()).format()
    }
}

fun Item.getTitle(): String = when (this) {
    is Ask -> title ?: "Ask HN"
    is Job -> title
    is Poll -> title
    is Story -> title
    else -> throw IllegalStateException("Unsupported item type")
}

fun Item.getUrl(): String? = when (this) {
    is Job -> url
    is Story -> url
    else -> null
}

fun Item.getPoint(): Int = when (this) {
    is Ask -> score
    is Comment -> throw IllegalStateException("Unsupported item type")
    is Job -> score
    is Poll -> score
    is PollOption -> score
    is Story -> score
}
