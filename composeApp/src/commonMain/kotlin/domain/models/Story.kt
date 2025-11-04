package domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a story.
 */
@Serializable
@SerialName("story")
data class Story(
    @SerialName("by")
    val userName: String,
    @SerialName("descendants")
    val countOfComment: Int,
    @SerialName("id")
    val id: Long,
    @SerialName("kids")
    val commentIds: List<Long> = emptyList(),
    @SerialName("score")
    val score: Int,
    @SerialName("time")
    val time: Long,
    @SerialName("title")
    val title: String,
    @SerialName("text")
    val text: String? = null,
    @SerialName("url")
    val url: String? = null,
    @SerialName("type")
    val type: String,
) : Item()

val sampleStoryJson = """
    {
      "by" : "dhouston",
      "descendants" : 71,
      "id" : 8863,
      "kids" : [ 8952, 9224, 8917, 8884, 8887, 8943, 8869, 8958, 9005, 9671, 8940, 9067, 8908, 9055, 8865, 8881, 8872, 8873, 8955, 10403, 8903, 8928, 9125, 8998, 8901, 8902, 8907, 8894, 8878, 8870, 8980, 8934, 8876 ],
      "score" : 111,
      "time" : 1175714200,
      "title" : "My YC app: Dropbox - Throw away your USB drive",
      "type" : "story",
      "url" : "http://www.getdropbox.com/u/2/screencast.html"
    }
""".trimIndent()
