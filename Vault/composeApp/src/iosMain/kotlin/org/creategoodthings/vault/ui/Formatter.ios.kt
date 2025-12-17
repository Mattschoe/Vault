package org.creategoodthings.vault.ui

import androidx.compose.ui.text.intl.Locale
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toNSDateComponents
import platform.Foundation.NSCalendar
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSDateFormatterNoStyle
import platform.Foundation.NSDateFormatterShortStyle
import platform.Foundation.NSLocale

actual fun LocalDate.toLocaleDisplayDate(locale: Locale): String {
    val formatter = NSDateFormatter()
    formatter.dateStyle = NSDateFormatterShortStyle
    formatter.timeStyle = NSDateFormatterNoStyle
    formatter.locale = NSLocale(locale.toLanguageTag())

    val components = this.toNSDateComponents()
    val calendar = NSCalendar.currentCalendar
    components.calendar = calendar
    val nsDate = components.date ?: return this.toString()

    return formatter.stringFromDate(nsDate)
}