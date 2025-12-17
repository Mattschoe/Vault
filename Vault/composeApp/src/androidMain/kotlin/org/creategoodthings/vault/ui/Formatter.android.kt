package org.creategoodthings.vault.ui

import androidx.compose.ui.text.intl.Locale
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

actual fun LocalDate.toLocaleDisplayDate(locale: Locale): String {
    val javaLocale = java.util.Locale.forLanguageTag(locale.toLanguageTag())
    val formatter = DateTimeFormatter
        .ofLocalizedDate(FormatStyle.SHORT)
        .withLocale(javaLocale)
    return this.toJavaLocalDate().format(formatter)
}