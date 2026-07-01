package com.giovankov.tinytaps.domain.usecase.kick

import com.giovankov.tinytaps.domain.model.Kick
import com.giovankov.tinytaps.domain.repository.KickSessionRepository
import javax.inject.Inject

class RecordKickUseCase @Inject constructor(
    private val repository: KickSessionRepository
) {
    suspend operator fun invoke(sessionId: String): Kick =
        repository.recordKick(sessionId)
}
