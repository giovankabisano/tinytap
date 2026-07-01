package com.giovankov.tinytaps.domain.usecase.movement

import com.giovankov.tinytaps.domain.model.MovementEpisode
import com.giovankov.tinytaps.domain.repository.MovementRepository
import javax.inject.Inject

class StopMovementUseCase @Inject constructor(
    private val repository: MovementRepository
) {
    suspend operator fun invoke(episodeId: String): MovementEpisode =
        repository.stopEpisode(episodeId)
}
