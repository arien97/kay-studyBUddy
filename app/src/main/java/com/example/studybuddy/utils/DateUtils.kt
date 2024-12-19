package com.example.studybuddy.utils

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

object DateUtils {

    private val dateFormat = DateTimeFormatter.ISO_LOCAL_DATE

    fun convertMillisToLocalDate(millis: Long): LocalDate {
        return Instant
            .ofEpochMilli(millis)
            .atZone(ZoneOffset.UTC)
            .toLocalDate()
    }

    fun convertLocalDateToMillis(localDate: LocalDate): Long {
        return localDate
            .atStartOfDay(ZoneOffset.UTC)
            .toInstant()
            .toEpochMilli()
    }

    fun dateToString(date: LocalDate): String {
        return dateFormat.format(date)
    }

    fun stringToDate(src: String): LocalDate {
        return LocalDate.parse(src, dateFormat)
    }

}