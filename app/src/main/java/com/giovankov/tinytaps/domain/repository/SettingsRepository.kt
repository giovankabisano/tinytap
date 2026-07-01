package com.giovankov.tinytaps.domain.repository

import com.giovankov.tinytaps.domain.model.AppSettings
import com.giovankov.tinytaps.domain.model.AppTheme
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalTime

interface SettingsRepository {
    val settings: Flow<AppSettings>
    suspend fun updateActiveWindow(start: LocalTime, end: LocalTime)
    suspend fun updateInactivityAlert(enabled: Boolean, thresholdHrs: Int? = null)
    suspend fun updateDailyReminder(enabled: Boolean, times: List<LocalTime>? = null)
    suspend fun updateKickTarget(target: Int)
    suspend fun updateDueDate(date: LocalDate?)
    suspend fun updateEmergencyContact(contact: String?)
    suspend fun updateTheme(theme: AppTheme)
    suspend fun updateHaptics(enabled: Boolean)
    suspend fun setOnboardingCompleted()
}
