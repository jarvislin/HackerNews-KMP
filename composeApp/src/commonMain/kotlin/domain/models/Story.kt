package domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a story.
 */
@Serializable
class Story(
    @SerialName("by")
    val userName: String,
    @SerialName("descendants")
    val countOfComment: Int,
    @SerialName("id")
    val id: Long,
    @SerialName("kids")
    val commentIds: List<Long>? = null,
    @SerialName("score")
    val score: Int,
    @SerialName("time")
    val time: Long,
    @SerialName("title")
    val title: String,
    @SerialName("url")
    val url: String,
) : Item()
