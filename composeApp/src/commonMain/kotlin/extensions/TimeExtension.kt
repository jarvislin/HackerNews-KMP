@file:OptIn(ExperimentalTime::class)

package extensions

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

object TimeExtension {
    private const val FORMAT = "yyyy-MM-dd HH:mm:ss"
    @OptIn(ExperimentalTime::class)
    fun Long.toLocalDateTime() = toInstant()
        .toLocalDateTime(TimeZone.currentSystemDefault())

    @OptIn(ExperimentalTime::class)
    fun Long.toInstant() = Instant.fromEpochSeconds(this)

    fun LocalDateTime.format(): String {
        val dateTimeFormat = LocalDateTime.Format {
            byUnicodePattern(FORMAT)
            //TODO: this better
        }
        return dateTimeFormat.format(this)
    }
}
