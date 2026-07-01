package com.giovankov.tinytaps.domain.usecase.kick

import com.giovankov.tinytaps.domain.model.KickSession
import com.giovankov.tinytaps.domain.repository.KickSessionRepository
import javax.inject.Inject

class EndKickSessionUseCase @Inject constructor(
    private val repository: KickSessionRepository
) {
    suspend operator fun invoke(sessionId: String, abandoned: Boolean = false): KickSession =
        repository.endSession(sessionId, abandoned)
}
