package com.giovankov.tinytaps.domain.model

import java.time.LocalDate
import java.time.LocalTime

data class AppSettings(
    val activeWindowStart: LocalTime = LocalTime.of(8, 0),
    val activeWindowEnd: LocalTime = LocalTime.of(22, 0),
    val inactivityAlertEnabled: Boolean = false,
    val inactivityThresholdHrs: Int = 3,
    val dailyReminderTimes: List<LocalTime> = emptyList(),
    val dailyReminderEnabled: Boolean = false,
    val kickTarget: Int = 10,
    val dueDate: LocalDate? = null,
    val emergencyContact: String? = null,
    val theme: AppTheme = AppTheme.SYSTEM,
    val hapticsEnabled: Boolean = true,
    val onboardingCompleted: Boolean = false
)
