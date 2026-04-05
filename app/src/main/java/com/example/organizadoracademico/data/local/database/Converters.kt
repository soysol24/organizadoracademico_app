package com.example.organizadoracademico.data.local.database

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromLongToDate(value: Long): java.util.Date = java.util.Date(value)

    @TypeConverter
    fun fromDateToLong(date: java.util.Date): Long = date.time
}