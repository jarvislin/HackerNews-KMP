package domain.models

import data.remote.models.RawItem
import extensions.TimeExtension.format
import extensions.TimeExtension.toInstant
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.time.DurationUnit

/**
 * Base class for all items.
 */
@Serializable
sealed class Item {
    fun getItemId(): Long = when (this) {
        is Ask -> id
        is Comment -> id
        is Job -> id
        is Poll -> id
        is PollOption -> id
        is Story -> id
    }

    companion object {
        private const val TYPE_STORY = "story"
        private const val TYPE_ASK = "ask"
        private const val TYPE_COMMENT = "comment"
        private const val TYPE_JOB = "job"
        private const val TYPE_POLL = "poll"
        private const val TYPE_POLL_OPTION = "pollopt"

        fun from(json: Json, text: String): Item? {
            val item = RawItem.from(json, text)
            if (item.deleted || item.dead) return null
            return when (item.type) {
                TYPE_STORY -> {
                    if (item.url != null) json.decodeFromString<Story>(text)
                    else json.decodeFromString<Ask>(text)
                }

                TYPE_ASK -> json.decodeFromString<Ask>(text)
                TYPE_COMMENT -> json.decodeFromString<Comment>(text)
                TYPE_JOB -> json.decodeFromString<Job>(text)
                TYPE_POLL -> json.decodeFromString<Poll>(text)
                TYPE_POLL_OPTION -> json.decodeFromString<PollOption>(text)
                else -> null // ignore unknown types
            }
        }
    }
}

fun Item.getCommentCount(): Int? = when (this) {
    is Ask -> countOfComment
    is Poll -> countOfComment
    is Story -> countOfComment
    else -> null
}

fun Item.getInstant(): Instant = when (this) {
    is Ask -> time
    is Job -> time
    is Poll -> time
    is PollOption -> time
    is Story -> time
    is Comment -> time
}.toInstant()

fun Item.getFormatedDiffTime(): String =
    when (val diff = Clock.System.now().minus(getInstant()).toLong(DurationUnit.SECONDS)) {
        in 0..60 -> "$diff seconds ago"
        in 60..3600 -> "${diff / 60} minutes ago"
        in 3600..86400 -> "${diff / 3600} hours ago"
        else -> "${diff / 86400} days ago"
    }

fun Item.getFormatedTime(): String =
    getInstant().toLocalDateTime(TimeZone.currentSystemDefault()).format()

fun Item.getTitle(): String = when (this) {
    is Ask -> title
    is Job -> title
    is Poll -> title
    is Story -> title
    else -> throw IllegalStateException("Unsupported item type")
}

fun Item.getText(): String? = when (this) {
    is Ask -> text
    is Job -> text
    is Poll -> text
    is Story -> text
    is Comment -> text
    else -> throw IllegalStateException("Unsupported item type")
}

fun Item.getUrl(): String? = when (this) {
    is Job -> url
    is Story -> url
    else -> null
}

fun Item.getUserName(): String = when (this) {
    is Ask -> userName
    is Comment -> userName
    is Job -> userName
    is Poll -> userName
    is PollOption -> userName
    is Story -> userName
}

fun Item.getPoint(): Int = when (this) {
    is Ask -> score
    is Comment -> throw IllegalStateException("Unsupported item type")
    is Job -> score
    is Poll -> score
    is PollOption -> score
    is Story -> score
}

fun Item.getCommentIds(): List<Long> = when (this) {
    is Story -> commentIds
    is Ask -> commentIds
    is Comment -> commentIds
    else -> emptyList()
}