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
    @SerialName("type")
    val type: String,
) : Item()

val samplePollOptionJson = """
    {
      "by" : "pg",
      "id" : 160705,
      "poll" : 160704,
      "score" : 335,
      "text" : "Yes, ban them; I'm tired of seeing Valleywag stories on News.YC.",
      "time" : 1207886576,
      "type" : "pollopt"
    }
""".trimIndent()