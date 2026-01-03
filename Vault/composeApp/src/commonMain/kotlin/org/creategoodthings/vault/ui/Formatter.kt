package org.creategoodthings.vault.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.intl.Locale
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.Month.APRIL
import kotlinx.datetime.Month.AUGUST
import kotlinx.datetime.Month.DECEMBER
import kotlinx.datetime.Month.FEBRUARY
import kotlinx.datetime.Month.JANUARY
import kotlinx.datetime.Month.JULY
import kotlinx.datetime.Month.JUNE
import kotlinx.datetime.Month.MARCH
import kotlinx.datetime.Month.MAY
import kotlinx.datetime.Month.NOVEMBER
import kotlinx.datetime.Month.OCTOBER
import kotlinx.datetime.Month.SEPTEMBER
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.AmPmMarker
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import org.jetbrains.compose.resources.stringResource
import vault.composeapp.generated.resources.Res
import vault.composeapp.generated.resources.days
import vault.composeapp.generated.resources.month_apr
import vault.composeapp.generated.resources.month_aug
import vault.composeapp.generated.resources.month_dec
import vault.composeapp.generated.resources.month_feb
import vault.composeapp.generated.resources.month_jan
import vault.composeapp.generated.resources.month_jul
import vault.composeapp.generated.resources.month_jun
import vault.composeapp.generated.resources.month_mar
import vault.composeapp.generated.resources.month_may
import vault.composeapp.generated.resources.month_nov
import vault.composeapp.generated.resources.month_oct
import vault.composeapp.generated.resources.month_sep
import vault.composeapp.generated.resources.months
import vault.composeapp.generated.resources.weeks
import vault.composeapp.generated.resources.years
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import org.creategoodthings.vault.ui.RemindMeType.*
import kotlin.math.min


@Composable
fun LocalDate.toDisplayText(): String {
    val monthResource = when(this.month) {
        JANUARY -> Res.string.month_jan
        FEBRUARY -> Res.string.month_feb
        MARCH -> Res.string.month_mar
        APRIL -> Res.string.month_apr
        MAY -> Res.string.month_may
        JUNE -> Res.string.month_jun
        JULY -> Res.string.month_jul
        AUGUST -> Res.string.month_aug
        SEPTEMBER -> Res.string.month_sep
        OCTOBER -> Res.string.month_oct
        NOVEMBER -> Res.string.month_nov
        DECEMBER -> Res.string.month_dec
    }
    val monthName = stringResource(monthResource)
    return "$day. $monthName ${this.year}"
}

@OptIn(ExperimentalTime::class)
fun Long.toLocalDate(): LocalDate {
    val instant = Instant.fromEpochMilliseconds(this)
    val timeZone = TimeZone.currentSystemDefault()
    return instant.toLocalDateTime(timeZone).date
}

@OptIn(ExperimentalTime::class)
fun LocalDate.isAfterToday(): Boolean {
    val timeZone = TimeZone.currentSystemDefault()
    val today = Clock.System.todayIn(timeZone)
    return this > today
}

/**
 * Formats a given localtime to look like a real time of day, so:
 *
 * LocalTime(8, 0) -> "08:00"
 *
 * LocalTime(15, 1) -> "15:01"
 *
 * LocalTime(11, 30) -> "11:30"
 *
 * @param amPm enables whether AM/PM text is appended to the string
 */
fun LocalTime.toDisplayString(amPm: Boolean = false): String {
    //TODO ADD AM/PM CONVERSION (Check first if it needs that)
    val stringBuilder = StringBuilder()

    if (amPm) {
        when (val hour = this.toAmPmHour()) {
            in 0..9 -> stringBuilder.append("0").append(hour)
            in 10..12 -> stringBuilder.append(hour)
        }
    } else {
        when(hour) {
            in 0..9 -> stringBuilder.append("0").append(hour)
            in 10..23 -> stringBuilder.append(hour)
        }
    }
    stringBuilder.append(":")
    when(minute) {
        in 0..9 -> stringBuilder.append("0").append(minute)
        in 10..59 -> stringBuilder.append(minute)
    }

    if (amPm) {
        stringBuilder.append(" ")
        stringBuilder.append(
            when {
                hour < 12 -> "AM"
                else -> "PM"
            }
        )
    }
    return stringBuilder.toString()
}

fun LocalTime.toAmPmHour(): Int {
    val hour = hour % 12
    return if (hour == 0) 12 else hour
}

enum class RemindMeType {
    DAYS,
    WEEKS,
    MONTHS,
    YEARS;
}

@Composable
fun RemindMeType.ToString(): String {
    return when (this) {
        DAYS -> stringResource(Res.string.days)
        WEEKS -> stringResource(Res.string.weeks)
        MONTHS -> stringResource(Res.string.months)
        YEARS -> stringResource(Res.string.years)
    }
}

fun LocalDate.calculateReminder(amount: Int, type: RemindMeType): LocalDate {
    return when (type) {
        DAYS -> this.plus(amount, DateTimeUnit.DAY)
        WEEKS -> this.plus(amount * 7, DateTimeUnit.DAY)
        MONTHS -> this.plus(amount, DateTimeUnit.MONTH)
        YEARS -> this.plus(amount, DateTimeUnit.YEAR)
    }
}

expect fun LocalDate.toLocaleDisplayDate(locale: Locale): String