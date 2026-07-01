package com.giovankov.tinytaps.domain.usecase.kick

import com.giovankov.tinytaps.domain.model.KickSession
import com.giovankov.tinytaps.domain.repository.KickSessionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetActiveKickSessionUseCase @Inject constructor(
    private val repository: KickSessionRepository
) {
    operator fun invoke(): Flow<KickSession?> = repository.getActiveSession()
}
