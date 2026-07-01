package com.giovankov.tinytaps.widget

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.widgetDataStore: DataStore<Preferences> by preferencesDataStore(name = "widget_state")

object WidgetStateKeys {
    val IS_RECORDING = booleanPreferencesKey("is_recording")
    val ACTIVE_EPISODE_ID = stringPreferencesKey("active_episode_id")
    val RECORDING_START_TIME = longPreferencesKey("recording_start_time")
    val LAST_MOVEMENT_TIME = longPreferencesKey("last_movement_time")
}

data class WidgetDisplayState(
    val isRecording: Boolean = false,
    val activeEpisodeId: String? = null,
    val recordingStartTime: Long = 0L,
    val lastMovementTime: Long? = null
)

suspend fun Context.getWidgetState(): WidgetDisplayState {
    return widgetDataStore.data.map { prefs ->
        WidgetDisplayState(
            isRecording = prefs[WidgetStateKeys.IS_RECORDING] ?: false,
            activeEpisodeId = prefs[WidgetStateKeys.ACTIVE_EPISODE_ID],
            recordingStartTime = prefs[WidgetStateKeys.RECORDING_START_TIME] ?: 0L,
            lastMovementTime = prefs[WidgetStateKeys.LAST_MOVEMENT_TIME]
        )
    }.first()
}

suspend fun Context.updateWidgetState(
    isRecording: Boolean? = null,
    activeEpisodeId: String? = null,
    recordingStartTime: Long? = null,
    lastMovementTime: Long? = null,
    clearRecording: Boolean = false
) {
    widgetDataStore.edit { prefs ->
        isRecording?.let { prefs[WidgetStateKeys.IS_RECORDING] = it }
        if (clearRecording) {
            prefs.remove(WidgetStateKeys.IS_RECORDING)
            prefs.remove(WidgetStateKeys.ACTIVE_EPISODE_ID)
            prefs.remove(WidgetStateKeys.RECORDING_START_TIME)
        } else {
            activeEpisodeId?.let { prefs[WidgetStateKeys.ACTIVE_EPISODE_ID] = it }
            recordingStartTime?.let { prefs[WidgetStateKeys.RECORDING_START_TIME] = it }
        }
        lastMovementTime?.let { prefs[WidgetStateKeys.LAST_MOVEMENT_TIME] = it }
    }
}
