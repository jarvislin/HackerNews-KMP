package domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A comment on a story/ask/poll/comment.
 */
@Serializable
@SerialName("comment")
data class Comment(
    @SerialName("by")
    val userName: String,
    @SerialName("id")
    val id: Long,
    @SerialName("kids")
    val commentIds: List<Long> = emptyList(),
    @SerialName("parent")
    val parentId: Long,
    @SerialName("text")
    val text: String,
    @SerialName("time")
    val time: Long,
    val depth: Int = 0
) : Item()
