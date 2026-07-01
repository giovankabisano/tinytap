package com.giovankov.tinytaps.domain.usecase.analytics

import com.giovankov.tinytaps.domain.repository.MovementRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject

data class HourlyHeatmapData(
    val hourCounts: Map<Int, Int> // hour (0-23) -> count
)

class GetHourlyHeatmapUseCase @Inject constructor(
    private val movementRepository: MovementRepository
) {
    operator fun invoke(sinceDaysAgo: Int): Flow<HourlyHeatmapData> {
        val since = System.currentTimeMillis() - (sinceDaysAgo.toLong() * 24 * 60 * 60 * 1000)
        return movementRepository.getEpisodeStartTimesSince(since).map { timestamps ->
            val hourCounts = timestamps
                .map { ts ->
                    Instant.ofEpochMilli(ts)
                        .atZone(ZoneId.systemDefault())
                        .hour
                }
                .groupingBy { it }
                .eachCount()
            HourlyHeatmapData(hourCounts = hourCounts)
        }
    }
}
