package com.giovankov.tinytaps.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.giovankov.tinytaps.util.TimeFormatter
import kotlinx.coroutines.delay

@Composable
fun TimerDisplay(
    startTimeMs: Long,
    isRunning: Boolean,
    modifier: Modifier = Modifier,
    label: String? = null
) {
    var elapsed by remember { mutableLongStateOf(System.currentTimeMillis() - startTimeMs) }

    LaunchedEffect(isRunning, startTimeMs) {
        if (isRunning) {
            while (true) {
                elapsed = System.currentTimeMillis() - startTimeMs
                delay(1000)
            }
        }
    }

    Column(
        modifier = modifier.semantics {
            liveRegion = LiveRegionMode.Polite
        },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        label?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Text(
            text = TimeFormatter.formatTimer(elapsed),
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
