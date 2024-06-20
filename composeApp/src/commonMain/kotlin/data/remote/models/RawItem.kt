package data.remote.models

import kotlinx.serialization.SerialName

class RawItem(
    @SerialName("type")
    val type: String,
    @SerialName("url")
    val url: String? // for determining the real type of the item, Ask and Story have the same type
)