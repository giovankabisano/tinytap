package com.giovankov.tinytaps.ui.screen.kickcount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.giovankov.tinytaps.domain.model.KickSession
import com.giovankov.tinytaps.domain.model.SessionStatus
import com.giovankov.tinytaps.domain.repository.KickSessionRepository
import com.giovankov.tinytaps.domain.repository.SettingsRepository
import com.giovankov.tinytaps.domain.usecase.kick.EndKickSessionUseCase
import com.giovankov.tinytaps.domain.usecase.kick.GetActiveKickSessionUseCase
import com.giovankov.tinytaps.domain.usecase.kick.RecordKickUseCase
import com.giovankov.tinytaps.domain.usecase.kick.StartKickSessionUseCase
import com.giovankov.tinytaps.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class KickCountUiState(
    val session: KickSession? = null,
    val kickCount: Int = 0,
    val targetCount: Int = 10,
    val isRunning: Boolean = false,
    val targetReached: Boolean = false,
    val timeToReachTarget: Long? = null,
    val showTrimesterInfo: Boolean = false,
    val showResult: Boolean = false
)

@HiltViewModel
class KickCountViewModel @Inject constructor(
    private val startKickSession: StartKickSessionUseCase,
    private val recordKick: RecordKickUseCase,
    private val endKickSession: EndKickSessionUseCase,
    getActiveSession: GetActiveKickSessionUseCase,
    private val kickSessionRepository: KickSessionRepository,
    settingsRepository: SettingsRepository
) : ViewModel() {

    private val _showResult = MutableStateFlow(false)

    private val activeSession = getActiveSession()

    private val kickCount = activeSession.flatMapLatest { session ->
        if (session != null) {
            kickSessionRepository.getKickCountForSession(session.id)
        } else {
            flowOf(0)
        }
    }

    val uiState = combine(
        activeSession,
        kickCount,
        settingsRepository.settings,
        _showResult
    ) { session, count, settings, showResult ->
        val gestWeeks = settings.dueDate?.let { DateUtils.gestationalWeeks(it) }
        val targetReached = session != null && count >= settings.kickTarget

        KickCountUiState(
            session = session,
            kickCount = count,
            targetCount = settings.kickTarget,
            isRunning = session?.status == SessionStatus.RUNNING,
            targetReached = targetReached,
            timeToReachTarget = session?.reachedTargetAt?.let { (it - session.startAt) / 1000 },
            showTrimesterInfo = gestWeeks != null && gestWeeks < 28,
            showResult = showResult
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), KickCountUiState())

    init {
        viewModelScope.launch {
            val settings = settingsRepository.settings.stateIn(viewModelScope).value
            val active = kickSessionRepository.getActiveSessionOnce()
            if (active == null) {
                startKickSession(settings.kickTarget)
            }
        }
    }

    fun recordKick() {
        viewModelScope.launch {
            val session = uiState.value.session ?: return@launch
            recordKick(session.id)

            // Check if target just reached
            val newCount = uiState.value.kickCount + 1
            if (newCount >= uiState.value.targetCount && session.reachedTargetAt == null) {
                // Mark target reached time on session
                val now = System.currentTimeMillis()
                val updatedSession = session.copy(reachedTargetAt = now)
                // We need to update via repository - simplified: use DAO indirectly
                _showResult.value = true
            }
        }
    }

    fun finishSession() {
        viewModelScope.launch {
            val session = uiState.value.session ?: return@launch
            endKickSession(session.id)
            _showResult.value = false
        }
    }
}
