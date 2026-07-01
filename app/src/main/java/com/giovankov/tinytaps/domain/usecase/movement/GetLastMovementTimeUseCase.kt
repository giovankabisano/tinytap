package com.giovankov.tinytaps.domain.usecase.movement

import com.giovankov.tinytaps.domain.repository.KickSessionRepository
import com.giovankov.tinytaps.domain.repository.MovementRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetLastMovementTimeUseCase @Inject constructor(
    private val movementRepository: MovementRepository,
    private val kickSessionRepository: KickSessionRepository
) {
    operator fun invoke(): Flow<Long?> = combine(
        movementRepository.getLastEpisodeEndTime(),
        kickSessionRepository.getLastKickTime()
    ) { episodeEnd, kickTime ->
        listOfNotNull(episodeEnd, kickTime).maxOrNull()
    }
}
