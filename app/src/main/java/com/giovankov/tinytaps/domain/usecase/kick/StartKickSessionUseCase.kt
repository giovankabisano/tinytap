package com.giovankov.tinytaps.domain.usecase.kick

import com.giovankov.tinytaps.domain.model.KickSession
import com.giovankov.tinytaps.domain.repository.KickSessionRepository
import javax.inject.Inject

class StartKickSessionUseCase @Inject constructor(
    private val repository: KickSessionRepository
) {
    suspend operator fun invoke(targetCount: Int): KickSession {
        val active = repository.getActiveSessionOnce()
        if (active != null) throw IllegalStateException("Session already running")
        return repository.startSession(targetCount)
    }
}
