package com.giovankov.tinytaps.ui.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.giovankov.tinytaps.R
import com.giovankov.tinytaps.ui.component.LastMovementCard
import com.giovankov.tinytaps.ui.component.MedicalDisclaimerBanner
import com.giovankov.tinytaps.ui.component.PrimaryActionButton
import com.giovankov.tinytaps.ui.component.SecondaryActionButton
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onStartRecording: () -> Unit,
    onStartKickCount: () -> Unit,
    onOpenEducation: () -> Unit
) {
    val dateFormatter = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", Locale("id"))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Greeting
        Text(
            text = stringResource(R.string.home_greeting),
            style = MaterialTheme.typography.headlineLarge
        )
        Text(
            text = LocalDate.now().format(dateFormatter),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Last movement card
        LastMovementCard(lastMovementTime = uiState.lastMovementTime)

        Spacer(modifier = Modifier.height(20.dp))

        // Action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PrimaryActionButton(
                text = stringResource(R.string.home_record_movement),
                onClick = onStartRecording,
                modifier = Modifier.weight(1f),
                icon = Icons.Filled.FavoriteBorder
            )
            SecondaryActionButton(
                text = stringResource(R.string.home_kick_count),
                onClick = onStartKickCount,
                modifier = Modifier.weight(1f),
                icon = Icons.Filled.Timer
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Today summary
        Text(
            text = stringResource(R.string.home_today_summary),
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.home_episodes_today, uiState.todayEpisodeCount),
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.home_kicks_today, uiState.todayKickSessionCount),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Medical disclaimer banner
        MedicalDisclaimerBanner(onClick = onOpenEducation)
    }
}
