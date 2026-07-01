package com.giovankov.tinytaps.ui.screen.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.giovankov.tinytaps.R
import com.giovankov.tinytaps.domain.model.AppTheme
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    onEvent: (SettingsEvent) -> Unit
) {
    val settings = uiState.settings
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        TopAppBar(title = { Text(stringResource(R.string.settings_title)) })

        // -- Pengingat Section --
        SectionHeader(stringResource(R.string.settings_reminder_section))

        ListItem(
            headlineContent = { Text(stringResource(R.string.settings_active_window)) },
            supportingContent = {
                Text("${settings.activeWindowStart.format(timeFormatter)} - ${settings.activeWindowEnd.format(timeFormatter)}")
            }
        )

        ListItem(
            headlineContent = { Text(stringResource(R.string.settings_inactivity_alert)) },
            supportingContent = { Text(stringResource(R.string.settings_inactivity_alert_desc)) },
            trailingContent = {
                Switch(
                    checked = settings.inactivityAlertEnabled,
                    onCheckedChange = { onEvent(SettingsEvent.UpdateInactivityAlert(it)) }
                )
            }
        )

        if (settings.inactivityAlertEnabled) {
            ListItem(
                headlineContent = { Text(stringResource(R.string.settings_inactivity_threshold)) },
                supportingContent = {
                    Column {
                        Text(stringResource(R.string.settings_inactivity_threshold_value, settings.inactivityThresholdHrs))
                        Slider(
                            value = settings.inactivityThresholdHrs.toFloat(),
                            onValueChange = { onEvent(SettingsEvent.UpdateInactivityThreshold(it.toInt())) },
                            valueRange = 1f..6f,
                            steps = 4
                        )
                    }
                }
            )
        }

        ListItem(
            headlineContent = { Text(stringResource(R.string.settings_daily_reminder)) },
            supportingContent = { Text(stringResource(R.string.settings_daily_reminder_desc)) },
            trailingContent = {
                Switch(
                    checked = settings.dailyReminderEnabled,
                    onCheckedChange = { onEvent(SettingsEvent.UpdateDailyReminder(it)) }
                )
            }
        )

        HorizontalDivider()

        // -- Tendangan Section --
        SectionHeader(stringResource(R.string.settings_kick_section))

        ListItem(
            headlineContent = { Text(stringResource(R.string.settings_kick_target)) },
            supportingContent = {
                Column {
                    Text(stringResource(R.string.settings_kick_target_value, settings.kickTarget))
                    Slider(
                        value = settings.kickTarget.toFloat(),
                        onValueChange = { onEvent(SettingsEvent.UpdateKickTarget(it.toInt())) },
                        valueRange = 5f..20f,
                        steps = 14
                    )
                }
            }
        )

        HorizontalDivider()

        // -- Kehamilan Section --
        SectionHeader(stringResource(R.string.settings_pregnancy_section))

        ListItem(
            headlineContent = { Text(stringResource(R.string.settings_due_date)) },
            supportingContent = {
                Text(
                    settings.dueDate?.format(DateTimeFormatter.ofPattern("d MMMM yyyy"))
                        ?: stringResource(R.string.settings_due_date_not_set)
                )
            }
        )

        ListItem(
            headlineContent = { Text(stringResource(R.string.settings_emergency_contact)) },
            supportingContent = {
                Text(settings.emergencyContact ?: stringResource(R.string.settings_emergency_contact_hint))
            }
        )

        HorizontalDivider()

        // -- Tampilan Section --
        SectionHeader(stringResource(R.string.settings_appearance_section))

        ListItem(
            headlineContent = { Text(stringResource(R.string.settings_theme)) },
            supportingContent = {
                Text(
                    when (settings.theme) {
                        AppTheme.SYSTEM -> stringResource(R.string.settings_theme_system)
                        AppTheme.LIGHT -> stringResource(R.string.settings_theme_light)
                        AppTheme.DARK -> stringResource(R.string.settings_theme_dark)
                    }
                )
            }
        )

        ListItem(
            headlineContent = { Text(stringResource(R.string.settings_haptics)) },
            trailingContent = {
                Switch(
                    checked = settings.hapticsEnabled,
                    onCheckedChange = { onEvent(SettingsEvent.UpdateHaptics(it)) }
                )
            }
        )
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    )
}
