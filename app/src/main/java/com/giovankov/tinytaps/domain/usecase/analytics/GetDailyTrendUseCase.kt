package com.giovankov.tinytaps.domain.usecase.analytics

import com.giovankov.tinytaps.domain.model.KickSession
import com.giovankov.tinytaps.domain.repository.KickSessionRepository
import com.giovankov.tinytaps.domain.repository.MovementRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

data class DailyTrendEntry(
    val date: LocalDate,
    val episodeCount: Int,
    val avgKickTimeSec: Long?
)

class GetDailyTrendUseCase @Inject constructor(
    private val movementRepository: MovementRepository,
    private val kickSessionRepository: KickSessionRepository
) {
    operator fun invoke(sinceDaysAgo: Int): Flow<List<DailyTrendEntry>> {
        val since = System.currentTimeMillis() - (sinceDaysAgo.toLong() * 24 * 60 * 60 * 1000)
        val zone = ZoneId.systemDefault()

        return combine(
            movementRepository.getEpisodeStartTimesSince(since),
            kickSessionRepository.getCompletedSessionsSince(since)
        ) { episodeTimestamps, kickSessions ->
            val episodesByDay = episodeTimestamps.groupBy { ts ->
                Instant.ofEpochMilli(ts).atZone(zone).toLocalDate()
            }

            val kicksByDay = kickSessions.groupBy { session ->
                Instant.ofEpochMilli(session.startAt).atZone(zone).toLocalDate()
            }

            val allDates = (episodesByDay.keys + kicksByDay.keys).distinct().sorted()

            allDates.map { date ->
                val episodeCount = episodesByDay[date]?.size ?: 0
                val avgKickTime = kicksByDay[date]?.let { sessions ->
                    calculateAvgKickTime(sessions)
                }
                DailyTrendEntry(
                    date = date,
                    episodeCount = episodeCount,
                    avgKickTimeSec = avgKickTime
                )
            }
        }
    }

    private fun calculateAvgKickTime(sessions: List<KickSession>): Long? {
        val durations = sessions.mapNotNull { session ->
            val reached = session.reachedTargetAt ?: return@mapNotNull null
            (reached - session.startAt) / 1000
        }
        return if (durations.isNotEmpty()) durations.average().toLong() else null
    }
}
