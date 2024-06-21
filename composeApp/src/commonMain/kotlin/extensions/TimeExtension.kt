package extensions

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toLocalDateTime

object TimeExtension {
    private const val FORMAT = "yyyy-MM-dd HH:mm:ss"
    fun Long.toLocalDateTime() = toInstant()
        .toLocalDateTime(TimeZone.currentSystemDefault())

    fun Long.toInstant() = Instant.fromEpochSeconds(this)

    fun LocalDateTime.format(): String {
        val dateTimeFormat = LocalDateTime.Format {
            byUnicodePattern(FORMAT)
        }
        return dateTimeFormat.format(this)
    }
}
