@file:OptIn(ExperimentalTime::class)

package domain.models

import data.remote.models.RawItem
import extensions.TimeExtension.format
import extensions.TimeExtension.toInstant
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlin.time.Clock
import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime

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
        private const val TYPE_COMMENT = "comment"
        private const val TYPE_JOB = "job"
        private const val TYPE_POLL = "poll"
        private const val TYPE_POLL_OPTION = "pollopt"

        fun from(json: Json, text: String): Item? {
            val item = RawItem.from(json, text)
            if (item.deleted || item.dead) return null
            return when (item.type) {
                TYPE_STORY -> {
                    // An "Ask" is by convention a story with no URL
                    if (item.url != null) json.decodeFromString<Story>(text)
                    else json.decodeFromString<Ask>(text)
                }
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

@OptIn(ExperimentalTime::class)
fun Item.getInstant(): Instant = when (this) {
    is Ask -> time
    is Job -> time
    is Poll -> time
    is PollOption -> time
    is Story -> time
    is Comment -> time
}.toInstant()

@OptIn(ExperimentalTime::class)
fun Item.getFormattedDiffTime(): String =
    when (val diff = Clock.System.now().minus(getInstant()).toLong(DurationUnit.SECONDS)) {
        in 0..60 -> "$diff seconds ago"
        in 60..3600 -> "${diff / 60} minutes ago"
        in 3600..86400 -> "${diff / 3600} hours ago"
        else -> "${diff / 86400} days ago"
    }

@OptIn(ExperimentalTime::class)
fun Item.getFormattedDiffTimeShort(): String =
    when (val diff = Clock.System.now().minus(getInstant()).toLong(DurationUnit.SECONDS)) {
        in 0..60 -> "${diff}s"
        in 60..3600 -> "${diff / 60}m"
        in 3600..86400 -> "${diff / 3600}h"
        else -> "${diff / 86400}d"
    }

@OptIn(ExperimentalTime::class)
fun Item.getFormattedTime(): String =
    getInstant().toLocalDateTime(TimeZone.currentSystemDefault()).format()

fun Item.getTitle(): String = when (this) {
    is Ask -> title
    is Job -> title
    is Poll -> title
    is Story -> title
    else -> error("Unsupported item type")
}

fun Item.getText(): String? = when (this) {
    is Ask -> text
    is Job -> text
    is Poll -> text
    is Story -> text
    is Comment -> text
    else -> error("Unsupported item type")
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
    is Comment -> error("Unsupported item type")
    is Job -> score
    is Poll -> score
    is PollOption -> score
    is Story -> score
}

fun Item.getCommentIds(): ImmutableList<Long> = when (this) {
    is Story -> commentIds.toPersistentList()
    is Ask -> commentIds.toPersistentList()
    is Comment -> commentIds.toPersistentList()
    else -> persistentListOf()
}
