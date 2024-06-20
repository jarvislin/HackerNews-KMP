package domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A comment on a story/ask/poll/comment.
 */
@Serializable
class Comment(
    @SerialName("by")
    val userName: String,
    @SerialName("id")
    val id: Long,
    @SerialName("kids")
    val commentIds: List<Long>,
    @SerialName("parent")
    val parentId: Long,
    @SerialName("text")
    val text: Int,
    @SerialName("time")
    val time: Long,
) : Item()
