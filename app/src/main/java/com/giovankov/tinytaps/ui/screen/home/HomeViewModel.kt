package com.giovankov.tinytaps.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.giovankov.tinytaps.domain.model.MovementEpisode
import com.giovankov.tinytaps.domain.repository.SettingsRepository
import com.giovankov.tinytaps.domain.usecase.analytics.DailyStats
import com.giovankov.tinytaps.domain.usecase.analytics.GetDailyStatsUseCase
import com.giovankov.tinytaps.domain.usecase.movement.GetActiveEpisodeUseCase
import com.giovankov.tinytaps.domain.usecase.movement.GetLastMovementTimeUseCase
import com.giovankov.tinytaps.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import javax.inject.Inject

data class HomeUiState(
    val lastMovementTime: Long? = null,
    val activeEpisode: MovementEpisode? = null,
    val todayEpisodeCount: Int = 0,
    val todayKickSessionCount: Int = 0,
    val gestationalWeeks: Int? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    getLastMovementTime: GetLastMovementTimeUseCase,
    getActiveEpisode: GetActiveEpisodeUseCase,
    getDailyStats: GetDailyStatsUseCase,
    settingsRepository: SettingsRepository
) : ViewModel() {

    val uiState = combine(
        getLastMovementTime(),
        getActiveEpisode(),
        getDailyStats(),
        settingsRepository.settings
    ) { lastTime, activeEpisode, stats, settings ->
        HomeUiState(
            lastMovementTime = lastTime,
            activeEpisode = activeEpisode,
            todayEpisodeCount = stats.episodeCount,
            todayKickSessionCount = stats.kickSessionCount,
            gestationalWeeks = settings.dueDate?.let { DateUtils.gestationalWeeks(it) }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())
}
