package com.giovankov.tinytaps.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.giovankov.tinytaps.domain.model.AppSettings
import com.giovankov.tinytaps.domain.model.AppTheme
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val ACTIVE_WINDOW_START = stringPreferencesKey("active_window_start")
        val ACTIVE_WINDOW_END = stringPreferencesKey("active_window_end")
        val INACTIVITY_ALERT_ENABLED = booleanPreferencesKey("inactivity_alert_enabled")
        val INACTIVITY_THRESHOLD_HRS = intPreferencesKey("inactivity_threshold_hrs")
        val DAILY_REMINDER_TIMES = stringPreferencesKey("daily_reminder_times")
        val DAILY_REMINDER_ENABLED = booleanPreferencesKey("daily_reminder_enabled")
        val KICK_TARGET = intPreferencesKey("kick_target")
        val DUE_DATE = stringPreferencesKey("due_date")
        val EMERGENCY_CONTACT = stringPreferencesKey("emergency_contact")
        val THEME = stringPreferencesKey("theme")
        val HAPTICS_ENABLED = booleanPreferencesKey("haptics_enabled")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
    }

    private val timeFormatter = DateTimeFormatter.ISO_LOCAL_TIME
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    val settings: Flow<AppSettings> = context.dataStore.data.map { prefs ->
        AppSettings(
            activeWindowStart = prefs[Keys.ACTIVE_WINDOW_START]
                ?.let { LocalTime.parse(it, timeFormatter) }
                ?: LocalTime.of(8, 0),
            activeWindowEnd = prefs[Keys.ACTIVE_WINDOW_END]
                ?.let { LocalTime.parse(it, timeFormatter) }
                ?: LocalTime.of(22, 0),
            inactivityAlertEnabled = prefs[Keys.INACTIVITY_ALERT_ENABLED] ?: false,
            inactivityThresholdHrs = prefs[Keys.INACTIVITY_THRESHOLD_HRS] ?: 3,
            dailyReminderTimes = prefs[Keys.DAILY_REMINDER_TIMES]
                ?.split(",")
                ?.filter { it.isNotBlank() }
                ?.map { LocalTime.parse(it.trim(), timeFormatter) }
                ?: emptyList(),
            dailyReminderEnabled = prefs[Keys.DAILY_REMINDER_ENABLED] ?: false,
            kickTarget = prefs[Keys.KICK_TARGET] ?: 10,
            dueDate = prefs[Keys.DUE_DATE]?.let { LocalDate.parse(it, dateFormatter) },
            emergencyContact = prefs[Keys.EMERGENCY_CONTACT],
            theme = prefs[Keys.THEME]?.let { AppTheme.valueOf(it) } ?: AppTheme.SYSTEM,
            hapticsEnabled = prefs[Keys.HAPTICS_ENABLED] ?: true,
            onboardingCompleted = prefs[Keys.ONBOARDING_COMPLETED] ?: false
        )
    }

    suspend fun updateActiveWindow(start: LocalTime, end: LocalTime) {
        context.dataStore.edit { prefs ->
            prefs[Keys.ACTIVE_WINDOW_START] = start.format(timeFormatter)
            prefs[Keys.ACTIVE_WINDOW_END] = end.format(timeFormatter)
        }
    }

    suspend fun updateInactivityAlert(enabled: Boolean, thresholdHrs: Int? = null) {
        context.dataStore.edit { prefs ->
            prefs[Keys.INACTIVITY_ALERT_ENABLED] = enabled
            thresholdHrs?.let { prefs[Keys.INACTIVITY_THRESHOLD_HRS] = it }
        }
    }

    suspend fun updateDailyReminder(enabled: Boolean, times: List<LocalTime>? = null) {
        context.dataStore.edit { prefs ->
            prefs[Keys.DAILY_REMINDER_ENABLED] = enabled
            times?.let {
                prefs[Keys.DAILY_REMINDER_TIMES] =
                    it.joinToString(",") { t -> t.format(timeFormatter) }
            }
        }
    }

    suspend fun updateKickTarget(target: Int) {
        context.dataStore.edit { prefs ->
            prefs[Keys.KICK_TARGET] = target
        }
    }

    suspend fun updateDueDate(date: LocalDate?) {
        context.dataStore.edit { prefs ->
            if (date != null) {
                prefs[Keys.DUE_DATE] = date.format(dateFormatter)
            } else {
                prefs.remove(Keys.DUE_DATE)
            }
        }
    }

    suspend fun updateEmergencyContact(contact: String?) {
        context.dataStore.edit { prefs ->
            if (contact != null) {
                prefs[Keys.EMERGENCY_CONTACT] = contact
            } else {
                prefs.remove(Keys.EMERGENCY_CONTACT)
            }
        }
    }

    suspend fun updateTheme(theme: AppTheme) {
        context.dataStore.edit { prefs ->
            prefs[Keys.THEME] = theme.name
        }
    }

    suspend fun updateHaptics(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.HAPTICS_ENABLED] = enabled
        }
    }

    suspend fun setOnboardingCompleted() {
        context.dataStore.edit { prefs ->
            prefs[Keys.ONBOARDING_COMPLETED] = true
        }
    }
}
