package domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a poll.
 */
@Serializable
@SerialName("poll")
data class Poll(
    @SerialName("by")
    val userName: String,
    @SerialName("descendants")
    val countOfComment: Int,
    @SerialName("id")
    val id: Long,
    @SerialName("kids")
    val commentIds: List<Long>,
    @SerialName("parts")
    val optionIds: List<Long>,
    @SerialName("score")
    val score: Int,
    @SerialName("text")
    val text: String? = null,
    @SerialName("time")
    val time: Long,
    @SerialName("title")
    val title: String,
    @SerialName("type")
    val type: String,
) : Item()

val samplePollJson = """
    {
      "by" : "pg",
      "descendants" : 54,
      "id" : 126809,
      "kids" : [ 126822, 126823, 126993, 126824, 126934, 127411, 126888, 127681, 126818, 126816, 126854, 127095, 126861, 127313, 127299, 126859, 126852, 126882, 126832, 127072, 127217, 126889, 127535, 126917, 126875 ],
      "parts" : [ 126810, 126811, 126812 ],
      "score" : 46,
      "text" : "",
      "time" : 1204403652,
      "title" : "Poll: What would happen if News.YC had explicit support for polls?",
      "type" : "poll"
    }
""".trimIndent()