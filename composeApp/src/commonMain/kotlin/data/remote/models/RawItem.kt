package data.remote.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
class RawItem(
    @SerialName("id")
    val id: Long,
    @SerialName("deleted")
    val deleted: Boolean = false,
    @SerialName("type")
    val type: String,
    @SerialName("by")
    val userName: String? = null, // if deleted
    @SerialName("time")
    val time: Long,
    @SerialName("text")
    val text: String? = null, // HTML
    @SerialName("dead")
    val dead: Boolean = false,
    @SerialName("parent")
    val parentId: Long? = null,
    @SerialName("poll")
    val pollId: Long? = null,
    @SerialName("kids")
    val commentIds: List<Long>? = null,
    @SerialName("url")
    val url: String? = null,
    @SerialName("score")
    val score: Long? = null,
    @SerialName("descendants")
    val countOfComment: Int = 0,
    @SerialName("title")
    val title: String? = null, // HTML
    @SerialName("parts")
    val optionIds: List<Long>? = null,
) {
    companion object {
        fun from(json: Json, text: String): RawItem = json.decodeFromString<RawItem>(text)
    }
}
