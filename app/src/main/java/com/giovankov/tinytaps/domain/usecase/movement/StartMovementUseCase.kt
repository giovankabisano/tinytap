package com.giovankov.tinytaps.domain.usecase.movement

import com.giovankov.tinytaps.domain.model.MovementEpisode
import com.giovankov.tinytaps.domain.model.MovementSource
import com.giovankov.tinytaps.domain.repository.MovementRepository
import javax.inject.Inject

class StartMovementUseCase @Inject constructor(
    private val repository: MovementRepository
) {
    suspend operator fun invoke(source: MovementSource): MovementEpisode {
        val active = repository.getActiveEpisodeOnce()
        if (active != null) throw IllegalStateException("Episode already running")
        return repository.startEpisode(source)
    }
}
