package com.giovankov.tinytaps.util

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object TimeFormatter {

    private val timeFormat = DateTimeFormatter.ofPattern("HH:mm")
    private val dateFormat = DateTimeFormatter.ofPattern("d MMMM yyyy")
    private val dayDateFormat = DateTimeFormatter.ofPattern("EEEE, d MMMM")

    fun formatRelativeTime(timestampMs: Long): String {
        val diff = System.currentTimeMillis() - timestampMs
        val minutes = diff / (60 * 1000)
        val hours = diff / (60 * 60 * 1000)
        val days = diff / (24 * 60 * 60 * 1000)

        return when {
            minutes < 1 -> "baru saja"
            minutes < 60 -> "$minutes menit lalu"
            hours < 24 -> "$hours jam lalu"
            days < 7 -> "$days hari lalu"
            else -> formatDate(timestampMs)
        }
    }

    fun formatDuration(seconds: Int): String {
        val mins = seconds / 60
        val secs = seconds % 60
        return when {
            mins > 0 -> "$mins mnt $secs dtk"
            else -> "$secs dtk"
        }
    }

    fun formatDurationLong(seconds: Long): String = formatDuration(seconds.toInt())

    fun formatTimer(elapsedMs: Long): String {
        val totalSeconds = elapsedMs / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return "%02d:%02d".format(minutes, seconds)
    }

    fun formatTime(timestampMs: Long): String {
        val instant = Instant.ofEpochMilli(timestampMs)
        return instant.atZone(ZoneId.systemDefault()).format(timeFormat)
    }

    fun formatDate(timestampMs: Long): String {
        val instant = Instant.ofEpochMilli(timestampMs)
        return instant.atZone(ZoneId.systemDefault()).format(dateFormat)
    }

    fun formatDayDate(timestampMs: Long): String {
        val instant = Instant.ofEpochMilli(timestampMs)
        return instant.atZone(ZoneId.systemDefault()).format(dayDateFormat)
    }
}
