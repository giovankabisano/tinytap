package com.giovankov.tinytaps.ui.screen.pattern

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.giovankov.tinytaps.domain.usecase.analytics.DailyTrendEntry
import com.giovankov.tinytaps.domain.usecase.analytics.GetDailyTrendUseCase
import com.giovankov.tinytaps.domain.usecase.analytics.GetHourlyHeatmapUseCase
import com.giovankov.tinytaps.domain.usecase.analytics.HourlyHeatmapData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class PatternUiState(
    val heatmapData: HourlyHeatmapData = HourlyHeatmapData(emptyMap()),
    val trendData: List<DailyTrendEntry> = emptyList(),
    val daysRange: Int = 7,
    val hasData: Boolean = false
)

@HiltViewModel
class PatternViewModel @Inject constructor(
    private val getHourlyHeatmap: GetHourlyHeatmapUseCase,
    private val getDailyTrend: GetDailyTrendUseCase
) : ViewModel() {

    private val _daysRange = MutableStateFlow(7)

    val uiState = _daysRange.flatMapLatest { days ->
        combine(
            getHourlyHeatmap(days),
            getDailyTrend(days)
        ) { heatmap, trend ->
            PatternUiState(
                heatmapData = heatmap,
                trendData = trend,
                daysRange = days,
                hasData = heatmap.hourCounts.isNotEmpty() || trend.isNotEmpty()
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PatternUiState())

    fun setDaysRange(days: Int) {
        _daysRange.value = days
    }
}
