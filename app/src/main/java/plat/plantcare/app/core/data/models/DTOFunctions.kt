package plat.plantcare.app.core.data.models

import com.google.firebase.Timestamp
import java.time.LocalDate
import java.time.ZoneId

/** Instant -> calendar date as seen in [zone]. */
fun Timestamp.toLocalDate(zone: ZoneId = ZoneId.systemDefault()): LocalDate =
    toDate().toInstant().atZone(zone).toLocalDate()

/** Calendar date -> the instant at the start of that day in [zone]. */
fun LocalDate.toTimestamp(zone: ZoneId = ZoneId.systemDefault()): Timestamp {
    val instant = atStartOfDay(zone).toInstant()
    return Timestamp(instant.epochSecond, instant.nano)
}