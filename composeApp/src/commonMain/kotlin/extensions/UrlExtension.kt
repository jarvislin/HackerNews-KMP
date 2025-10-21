package extensions

import io.ktor.http.Url


fun Url.trimmedHostName(): String {
    val hostName = host
    return if (hostName.startsWith("www.")) {
        hostName.substring(4)
    } else {
        hostName
    }
}