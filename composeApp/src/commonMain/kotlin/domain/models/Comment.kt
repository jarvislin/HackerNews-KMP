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
    val depth: Int = 0,
    @SerialName("type")
    val type: String,
) : Item()


val sampleCommentJson = """
    {
      "by" : "norvig",
      "id" : 2921983,
      "kids" : [ 2922097, 2922429, 2924562, 2922709, 2922573, 2922140, 2922141 ],
      "parent" : 2921506,
      "text" : "Aw shucks, guys ... you make me blush with your compliments.<p>Tell you what, Ill make a deal: I'll keep writing if you keep reading. K?",
      "time" : 1314211127,
      "type" : "comment"
    }
""".trimIndent()