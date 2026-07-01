package com.giovankov.tinytaps.domain.usecase.movement

import com.giovankov.tinytaps.domain.model.MovementEpisode
import com.giovankov.tinytaps.domain.repository.MovementRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetActiveEpisodeUseCase @Inject constructor(
    private val repository: MovementRepository
) {
    operator fun invoke(): Flow<MovementEpisode?> = repository.getActiveEpisode()
}
