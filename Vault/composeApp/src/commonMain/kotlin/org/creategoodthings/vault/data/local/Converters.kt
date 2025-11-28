package org.creategoodthings.vault.data.local

import androidx.room.TypeConverter
import kotlinx.datetime.LocalDate

class Converters {
    @TypeConverter
    fun fromLocalDate(date: LocalDate?): Long? {
        return date?.toEpochDays()
    }

    @TypeConverter
    fun toLocalDate(date: Long?): LocalDate? {
        return date?.let { LocalDate.fromEpochDays(it) }
    }
}