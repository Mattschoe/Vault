package org.creategoodthings.vault.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.intl.Locale
import kotlinx.datetime.LocalDate
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
import org.jetbrains.compose.resources.stringResource
import vault.composeapp.generated.resources.Res
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

expect fun LocalDate.toLocaleDisplayDate(locale: Locale): String