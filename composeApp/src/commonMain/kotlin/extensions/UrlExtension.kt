package extensions

import io.ktor.http.Url
import utils.Constants
import kotlin.time.ExperimentalTime


@OptIn(ExperimentalTime::class)
fun String.toUrl(): Url? = runCatching { Url(this) }.getOrNull()

fun Url.trimmedHostName(): String {
    val hostName = host
    return if (hostName.startsWith("www.")) {
        hostName.substring(4)
    } else {
        hostName
    }
}

fun Url.faviconUrl(): String =
    "https://www.google.com/s2/favicons?domain=$host&sz=128"

fun Url.isPdf(): Boolean =
    rawSegments.lastOrNull()?.endsWith(Constants.PDF_EXTENSION) ?: false
