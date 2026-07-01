package com.giovankov.tinytaps.data.repository

import com.giovankov.tinytaps.data.local.datastore.SettingsDataStore
import com.giovankov.tinytaps.domain.model.AppSettings
import com.giovankov.tinytaps.domain.model.AppTheme
import com.giovankov.tinytaps.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: SettingsDataStore
) : SettingsRepository {

    override val settings: Flow<AppSettings> = dataStore.settings

    override suspend fun updateActiveWindow(start: LocalTime, end: LocalTime) =
        dataStore.updateActiveWindow(start, end)

    override suspend fun updateInactivityAlert(enabled: Boolean, thresholdHrs: Int?) =
        dataStore.updateInactivityAlert(enabled, thresholdHrs)

    override suspend fun updateDailyReminder(enabled: Boolean, times: List<LocalTime>?) =
        dataStore.updateDailyReminder(enabled, times)

    override suspend fun updateKickTarget(target: Int) =
        dataStore.updateKickTarget(target)

    override suspend fun updateDueDate(date: LocalDate?) =
        dataStore.updateDueDate(date)

    override suspend fun updateEmergencyContact(contact: String?) =
        dataStore.updateEmergencyContact(contact)

    override suspend fun updateTheme(theme: AppTheme) =
        dataStore.updateTheme(theme)

    override suspend fun updateHaptics(enabled: Boolean) =
        dataStore.updateHaptics(enabled)

    override suspend fun setOnboardingCompleted() =
        dataStore.setOnboardingCompleted()
}
