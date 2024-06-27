package domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a job.
 */
@Serializable
@SerialName("job")
data class Job(
    @SerialName("by")
    val userName: String,
    @SerialName("id")
    val id: Long,
    @SerialName("score")
    val score: Int,
    @SerialName("text")
    val text: String? = null,
    @SerialName("time")
    val time: Long,
    @SerialName("title")
    val title: String,
    @SerialName("url")
    val url: String? = null,
) : Item()
