package com.giovankov.tinytaps.util

import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit

object DateUtils {

    fun startOfToday(): Long {
        return LocalDate.now()
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }

    fun startOfDay(date: LocalDate): Long {
        return date.atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }

    fun daysAgoMillis(days: Int): Long {
        return LocalDate.now()
            .minusDays(days.toLong())
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }

    fun gestationalWeeks(dueDate: LocalDate): Int? {
        val today = LocalDate.now()
        // Estimated conception = due date - 280 days (40 weeks)
        val conceptionDate = dueDate.minusDays(280)
        val daysSinceConception = ChronoUnit.DAYS.between(conceptionDate, today)
        if (daysSinceConception < 0) return null
        return (daysSinceConception / 7).toInt()
    }

    fun isInActiveWindow(start: LocalTime, end: LocalTime): Boolean {
        val now = LocalTime.now()
        return if (start.isBefore(end)) {
            now.isAfter(start) && now.isBefore(end)
        } else {
            // Window crosses midnight
            now.isAfter(start) || now.isBefore(end)
        }
    }
}
