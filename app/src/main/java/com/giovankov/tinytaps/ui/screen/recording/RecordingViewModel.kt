package com.giovankov.tinytaps.ui.screen.recording

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.giovankov.tinytaps.domain.model.MovementEpisode
import com.giovankov.tinytaps.domain.model.MovementSource
import com.giovankov.tinytaps.domain.usecase.movement.GetActiveEpisodeUseCase
import com.giovankov.tinytaps.domain.usecase.movement.StartMovementUseCase
import com.giovankov.tinytaps.domain.usecase.movement.StopMovementUseCase
import com.giovankov.tinytaps.domain.repository.MovementRepository
import com.giovankov.tinytaps.notification.NotificationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RecordingUiState(
    val activeEpisode: MovementEpisode? = null,
    val isRecording: Boolean = false,
    val showLongEpisodeDialog: Boolean = false,
    val lastStoppedEpisode: MovementEpisode? = null
)

@HiltViewModel
class RecordingViewModel @Inject constructor(
    private val startMovement: StartMovementUseCase,
    private val stopMovement: StopMovementUseCase,
    getActiveEpisode: GetActiveEpisodeUseCase,
    private val movementRepository: MovementRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _showLongEpisodeDialog = MutableStateFlow(false)
    private val _lastStoppedEpisode = MutableStateFlow<MovementEpisode?>(null)

    val uiState = combine(
        getActiveEpisode(),
        _showLongEpisodeDialog,
        _lastStoppedEpisode
    ) { activeEpisode, showDialog, lastStopped ->
        RecordingUiState(
            activeEpisode = activeEpisode,
            isRecording = activeEpisode != null,
            showLongEpisodeDialog = showDialog,
            lastStoppedEpisode = lastStopped
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), RecordingUiState())

    fun startRecording() {
        viewModelScope.launch {
            try {
                startMovement(MovementSource.APP)
                NotificationHelper.showRecordingNotification(context)
            } catch (_: IllegalStateException) {
                // Already recording
            }
        }
    }

    fun stopRecording() {
        viewModelScope.launch {
            val active = uiState.value.activeEpisode ?: return@launch
            val stopped = stopMovement(active.id)
            _lastStoppedEpisode.value = stopped
            NotificationHelper.cancelRecordingNotification(context)
        }
    }

    fun discardRecording() {
        viewModelScope.launch {
            val active = uiState.value.activeEpisode ?: return@launch
            movementRepository.deleteEpisode(active.id)
            _showLongEpisodeDialog.value = false
            NotificationHelper.cancelRecordingNotification(context)
        }
    }
}
