package domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class Ask(
    @SerialName("by")
    val userName: String,
    @SerialName("descendants")
    val countOfComment: Long,
    @SerialName("id")
    val id: Long,
    @SerialName("kids")
    val commentIds: List<Long>,
    @SerialName("score")
    val score: Int,
    @SerialName("text")
    val text: String,
    @SerialName("time")
    val time: Long,
    @SerialName("title")
    val title: String,
) : Item()
