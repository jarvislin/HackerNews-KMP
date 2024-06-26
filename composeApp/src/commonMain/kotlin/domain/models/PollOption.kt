package domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a poll option.
 */
@Serializable
@SerialName("pollopt")
data class PollOption(
    @SerialName("by")
    val userName: String,
    @SerialName("id")
    val id: Long,
    @SerialName("poll")
    val pollId: Long,
    @SerialName("score")
    val score: Int,
    @SerialName("text")
    val text: String,
    @SerialName("time")
    val time: Long,
) : Item()
