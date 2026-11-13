package plat.plantcare.app.core.data.models

import com.google.firebase.Timestamp
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId
import java.time.temporal.ChronoUnit

/** Instant -> calendar date as seen in [zone]. */
fun Timestamp.toLocalDate(zone: ZoneId = ZoneId.systemDefault()): LocalDate =
    toDate().toInstant().atZone(zone).toLocalDate()

/** Calendar date -> the instant at the start of that day in [zone]. */
fun LocalDate.toTimestamp(zone: ZoneId = ZoneId.systemDefault()): Timestamp {
    val instant = atStartOfDay(zone).toInstant()
    return Timestamp(instant.epochSecond, instant.nano)
}

fun LocalDate.toUrgency(today: LocalDate = LocalDate.now()): TaskUrgency =
    when (ChronoUnit.DAYS.between(today, this)) {
        in Long.MIN_VALUE..-1L -> TaskUrgency.ATRASADO
        0L -> TaskUrgency.HOY
        in 1L..3L -> TaskUrgency.PRIORITARIO
        else -> TaskUrgency.PROXIMO
    }

fun LocalDate.getRemainingDays(today: LocalDate = LocalDate.now()): Int =
    ChronoUnit.DAYS.between(today, this).toInt()

fun LocalDate.getAge(today: LocalDate = LocalDate.now()): String {
    require(!this.isAfter(today)) { "Date must not be in the future" }

    val age = Period.between(this, today)

    val years = if (age.years == 1) "año" else "años"
    val months = if (age.months == 1) "mes" else "meses"

    return "${age.years} $years and ${age.months} $months"
}