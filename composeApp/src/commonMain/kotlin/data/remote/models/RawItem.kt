package data.remote.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class RawItem(
    @SerialName("type")
    val type: String,
    @SerialName("url")
    val url: String? = null // for determining the real type of the item, Ask and Story have the same type
)