package com.giovankov.tinytaps.ui.screen.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.giovankov.tinytaps.R
import com.giovankov.tinytaps.ui.component.DaySectionHeader
import com.giovankov.tinytaps.ui.component.EpisodeCard
import com.giovankov.tinytaps.ui.component.KickSessionCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    uiState: HistoryUiState
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(stringResource(R.string.history_title)) }
        )

        if (uiState.isEmpty) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.history_empty),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                uiState.items.forEach { (date, items) ->
                    item(key = "header_$date") {
                        DaySectionHeader(date = date)
                    }
                    items(
                        items = items,
                        key = { item ->
                            when (item) {
                                is HistoryItem.EpisodeItem -> "ep_${item.episode.id}"
                                is HistoryItem.KickSessionItem -> "ks_${item.session.id}"
                            }
                        }
                    ) { item ->
                        when (item) {
                            is HistoryItem.EpisodeItem -> {
                                EpisodeCard(episode = item.episode)
                            }
                            is HistoryItem.KickSessionItem -> {
                                KickSessionCard(
                                    session = item.session,
                                    kickCount = item.kickCount
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
