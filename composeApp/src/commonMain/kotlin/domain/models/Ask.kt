package domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents an Ask item.
 */
@Serializable
@SerialName("ask")
data class Ask(
    @SerialName("by")
    val userName: String,
    @SerialName("descendants")
    val countOfComment: Int? = null,
    @SerialName("id")
    val id: Long,
    @SerialName("kids")
    val commentIds: List<Long> = emptyList(),
    @SerialName("score")
    val score: Int,
    @SerialName("text")
    val text: String? = null,
    @SerialName("time")
    val time: Long,
    @SerialName("title")
    val title: String,
) : Item()
