package com.giovankov.tinytaps.ui.screen.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.giovankov.tinytaps.domain.model.KickSession
import com.giovankov.tinytaps.domain.model.MovementEpisode
import com.giovankov.tinytaps.domain.repository.KickSessionRepository
import com.giovankov.tinytaps.domain.repository.MovementRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

enum class HistoryFilter { ALL, EPISODES, KICKS }

sealed class HistoryItem {
    abstract val timestamp: Long

    data class EpisodeItem(val episode: MovementEpisode) : HistoryItem() {
        override val timestamp = episode.startAt
    }

    data class KickSessionItem(val session: KickSession, val kickCount: Int = 0) : HistoryItem() {
        override val timestamp = session.startAt
    }
}

data class HistoryUiState(
    val items: Map<LocalDate, List<HistoryItem>> = emptyMap(),
    val filter: HistoryFilter = HistoryFilter.ALL,
    val isEmpty: Boolean = true
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    movementRepository: MovementRepository,
    kickSessionRepository: KickSessionRepository
) : ViewModel() {

    private val _filter = MutableStateFlow(HistoryFilter.ALL)
    private val zone = ZoneId.systemDefault()

    val uiState = combine(
        movementRepository.getAllEpisodes(),
        kickSessionRepository.getAllSessions(),
        _filter
    ) { episodes, sessions, filter ->
        val items = buildList {
            if (filter != HistoryFilter.KICKS) {
                addAll(episodes.map { HistoryItem.EpisodeItem(it) })
            }
            if (filter != HistoryFilter.EPISODES) {
                addAll(sessions.map { HistoryItem.KickSessionItem(it) })
            }
        }.sortedByDescending { it.timestamp }

        val grouped = items.groupBy { item ->
            Instant.ofEpochMilli(item.timestamp).atZone(zone).toLocalDate()
        }

        HistoryUiState(
            items = grouped,
            filter = filter,
            isEmpty = items.isEmpty()
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HistoryUiState())

    fun setFilter(filter: HistoryFilter) {
        _filter.value = filter
    }
}
