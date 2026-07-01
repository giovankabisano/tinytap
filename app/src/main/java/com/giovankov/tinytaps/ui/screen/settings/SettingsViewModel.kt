package com.giovankov.tinytaps.ui.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.giovankov.tinytaps.domain.model.AppSettings
import com.giovankov.tinytaps.domain.model.AppTheme
import com.giovankov.tinytaps.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

data class SettingsUiState(
    val settings: AppSettings = AppSettings()
)

sealed class SettingsEvent {
    data class UpdateActiveWindow(val start: LocalTime, val end: LocalTime) : SettingsEvent()
    data class UpdateInactivityAlert(val enabled: Boolean) : SettingsEvent()
    data class UpdateInactivityThreshold(val hours: Int) : SettingsEvent()
    data class UpdateDailyReminder(val enabled: Boolean) : SettingsEvent()
    data class UpdateDailyReminderTimes(val times: List<LocalTime>) : SettingsEvent()
    data class UpdateKickTarget(val target: Int) : SettingsEvent()
    data class UpdateDueDate(val date: LocalDate?) : SettingsEvent()
    data class UpdateEmergencyContact(val contact: String?) : SettingsEvent()
    data class UpdateTheme(val theme: AppTheme) : SettingsEvent()
    data class UpdateHaptics(val enabled: Boolean) : SettingsEvent()
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val uiState = settingsRepository.settings
        .map { SettingsUiState(settings = it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SettingsUiState())

    fun onEvent(event: SettingsEvent) {
        viewModelScope.launch {
            when (event) {
                is SettingsEvent.UpdateActiveWindow ->
                    settingsRepository.updateActiveWindow(event.start, event.end)
                is SettingsEvent.UpdateInactivityAlert ->
                    settingsRepository.updateInactivityAlert(event.enabled)
                is SettingsEvent.UpdateInactivityThreshold ->
                    settingsRepository.updateInactivityAlert(
                        uiState.value.settings.inactivityAlertEnabled,
                        event.hours
                    )
                is SettingsEvent.UpdateDailyReminder ->
                    settingsRepository.updateDailyReminder(event.enabled)
                is SettingsEvent.UpdateDailyReminderTimes ->
                    settingsRepository.updateDailyReminder(
                        uiState.value.settings.dailyReminderEnabled,
                        event.times
                    )
                is SettingsEvent.UpdateKickTarget ->
                    settingsRepository.updateKickTarget(event.target)
                is SettingsEvent.UpdateDueDate ->
                    settingsRepository.updateDueDate(event.date)
                is SettingsEvent.UpdateEmergencyContact ->
                    settingsRepository.updateEmergencyContact(event.contact)
                is SettingsEvent.UpdateTheme ->
                    settingsRepository.updateTheme(event.theme)
                is SettingsEvent.UpdateHaptics ->
                    settingsRepository.updateHaptics(event.enabled)
            }
        }
    }
}
