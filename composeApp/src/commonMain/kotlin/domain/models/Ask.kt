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
    @SerialName("type")
    val type: String,
) : Item()

val sampleAskJson = """
    {
      "by" : "tel",
      "descendants" : 16,
      "id" : 121003,
      "kids" : [ 121016, 121109, 121168 ],
      "score" : 25,
      "text" : "<i>or</i> HN: the Next Iteration<p>I get the impression that with Arc being released a lot of people who never had time for HN before are suddenly dropping in more often. (PG: what are the numbers on this? I'm envisioning a spike.)<p>Not to say that isn't great, but I'm wary of Diggification. Between links comparing programming to sex and a flurry of gratuitous, ostentatious  adjectives in the headlines it's a bit concerning.<p>80% of the stuff that makes the front page is still pretty awesome, but what's in place to keep the signal/noise ratio high? Does the HN model still work as the community scales? What's in store for (++ HN)?",
      "time" : 1203647620,
      "title" : "Ask HN: The Arc Effect",
      "type" : "story"
    }
""".trimIndent()
