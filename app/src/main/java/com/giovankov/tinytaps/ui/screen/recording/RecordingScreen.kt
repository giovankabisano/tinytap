package com.giovankov.tinytaps.ui.screen.recording

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.giovankov.tinytaps.R
import com.giovankov.tinytaps.ui.component.PrimaryActionButton
import com.giovankov.tinytaps.ui.component.TimerDisplay
import com.giovankov.tinytaps.util.TimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordingScreen(
    uiState: RecordingUiState,
    onStart: () -> Unit,
    onStop: () -> Unit,
    onDiscard: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.recording_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (uiState.isRecording && uiState.activeEpisode != null) {
                // Recording state
                Text(
                    text = stringResource(
                        R.string.recording_started_at,
                        TimeFormatter.formatTime(uiState.activeEpisode.startAt)
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                TimerDisplay(
                    startTimeMs = uiState.activeEpisode.startAt,
                    isRunning = true,
                    label = "Durasi gerakan"
                )

                Spacer(modifier = Modifier.height(48.dp))

                PrimaryActionButton(
                    text = stringResource(R.string.recording_stop),
                    onClick = onStop,
                    icon = Icons.Filled.Stop
                )
            } else if (uiState.lastStoppedEpisode != null) {
                // Just finished
                Text(
                    text = "Gerakan tercatat!",
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                uiState.lastStoppedEpisode.durationSec?.let { duration ->
                    Text(
                        text = "Durasi: ${TimeFormatter.formatDuration(duration)}",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
                PrimaryActionButton(
                    text = stringResource(R.string.recording_start),
                    onClick = onStart,
                    icon = Icons.Filled.FavoriteBorder
                )
            } else {
                // Idle state
                Text(
                    text = stringResource(R.string.recording_idle_hint),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(48.dp))

                PrimaryActionButton(
                    text = stringResource(R.string.recording_start),
                    onClick = onStart,
                    icon = Icons.Filled.FavoriteBorder
                )
            }
        }

        // Long episode dialog
        if (uiState.showLongEpisodeDialog) {
            AlertDialog(
                onDismissRequest = {},
                title = { Text(stringResource(R.string.recording_long_episode_title)) },
                text = { Text(stringResource(R.string.recording_long_episode_message)) },
                confirmButton = {
                    TextButton(onClick = onStop) {
                        Text(stringResource(R.string.recording_stop_now))
                    }
                },
                dismissButton = {
                    TextButton(onClick = onDiscard) {
                        Text(stringResource(R.string.recording_discard))
                    }
                }
            )
        }
    }
}
