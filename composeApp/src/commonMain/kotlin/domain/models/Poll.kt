package domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class Poll(
    @SerialName("by")
    val userName: String,
    @SerialName("descendants")
    val countOfComment: Int,
    @SerialName("id")
    val id: Long,
    @SerialName("kid")
    val commentIds: List<Long>,
    @SerialName("parts")
    val optionIds: List<Long>,
    @SerialName("score")
    val score: Int,
    @SerialName("text")
    val text: String,
    @SerialName("time")
    val time: Long,
    @SerialName("title")
    val title: String,
) : Item()
