package com.giovankov.tinytaps.ui.screen.kickcount

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.giovankov.tinytaps.R
import com.giovankov.tinytaps.ui.component.CircularProgressCount
import com.giovankov.tinytaps.ui.component.PrimaryActionButton
import com.giovankov.tinytaps.ui.component.SecondaryActionButton
import com.giovankov.tinytaps.ui.component.TimerDisplay
import com.giovankov.tinytaps.util.TimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KickCountScreen(
    uiState: KickCountUiState,
    onKick: () -> Unit,
    onFinish: () -> Unit,
    onBack: () -> Unit,
    onOpenEducation: () -> Unit
) {
    val haptic = LocalHapticFeedback.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.kick_count_title)) },
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Trimester info
            if (uiState.showTrimesterInfo) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        text = stringResource(R.string.kick_trimester_info),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(12.dp),
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Circular progress
            CircularProgressCount(
                count = uiState.kickCount,
                target = uiState.targetCount
            )

            // Timer
            if (uiState.session != null && uiState.isRunning) {
                TimerDisplay(
                    startTimeMs = uiState.session.startAt,
                    isRunning = true
                )
            }

            // Target reached message
            if (uiState.targetReached) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.kick_target_reached_title),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        uiState.timeToReachTarget?.let { secs ->
                            Text(
                                text = stringResource(
                                    R.string.kick_target_reached_message,
                                    uiState.targetCount,
                                    TimeFormatter.formatDurationLong(secs)
                                ),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Medical note
            Text(
                text = stringResource(R.string.kick_medical_note),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            // Action buttons
            if (uiState.isRunning) {
                PrimaryActionButton(
                    text = stringResource(R.string.kick_add),
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onKick()
                    }
                )
            }

            SecondaryActionButton(
                text = stringResource(R.string.kick_finish),
                onClick = onFinish
            )
        }
    }
}
