@file:OptIn(ExperimentalTime::class)

package extensions

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

object TimeExtension {
    @OptIn(ExperimentalTime::class)
    fun Long.toLocalDateTime() = toInstant()
        .toLocalDateTime(TimeZone.currentSystemDefault())

    @OptIn(ExperimentalTime::class)
    fun Long.toInstant() = Instant.fromEpochSeconds(this)

    fun LocalDateTime.format(): String {
        val dateTimeFormat = LocalDateTime.Format {
            date(LocalDate.Formats.ISO)
            char(' ')
            time(LocalTime.Formats.ISO)
        }
        return dateTimeFormat.format(this)
    }
}
