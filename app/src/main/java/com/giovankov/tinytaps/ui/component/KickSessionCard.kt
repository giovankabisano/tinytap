package com.giovankov.tinytaps.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.giovankov.tinytaps.domain.model.KickSession
import com.giovankov.tinytaps.domain.model.SessionStatus
import com.giovankov.tinytaps.util.TimeFormatter

@Composable
fun KickSessionCard(
    session: KickSession,
    kickCount: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Timer,
                contentDescription = "Sesi tendangan",
                tint = MaterialTheme.colorScheme.primary
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Hitung Tendangan",
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = TimeFormatter.formatTime(session.startAt) +
                            " \u2022 $kickCount gerakan",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            val durationText = when (session.status) {
                SessionStatus.COMPLETED -> {
                    session.reachedTargetAt?.let { reached ->
                        val secs = ((reached - session.startAt) / 1000).toInt()
                        TimeFormatter.formatDuration(secs)
                    } ?: ""
                }
                SessionStatus.RUNNING -> "Berlangsung"
                SessionStatus.ABANDONED -> "Dibatalkan"
            }

            Text(
                text = durationText,
                style = MaterialTheme.typography.labelLarge,
                color = if (session.status == SessionStatus.COMPLETED)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
