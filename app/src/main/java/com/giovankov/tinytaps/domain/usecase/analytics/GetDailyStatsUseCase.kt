package com.giovankov.tinytaps.domain.usecase.analytics

import com.giovankov.tinytaps.domain.repository.KickSessionRepository
import com.giovankov.tinytaps.domain.repository.MovementRepository
import com.giovankov.tinytaps.util.DateUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

data class DailyStats(
    val episodeCount: Int,
    val kickSessionCount: Int
)

class GetDailyStatsUseCase @Inject constructor(
    private val movementRepository: MovementRepository,
    private val kickSessionRepository: KickSessionRepository
) {
    operator fun invoke(): Flow<DailyStats> {
        val dayStart = DateUtils.startOfToday()
        return combine(
            movementRepository.getEpisodeCountSince(dayStart),
            kickSessionRepository.getSessionCountSince(dayStart)
        ) { episodes, sessions ->
            DailyStats(episodeCount = episodes, kickSessionCount = sessions)
        }
    }
}
