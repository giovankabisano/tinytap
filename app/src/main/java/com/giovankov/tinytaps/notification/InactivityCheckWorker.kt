package com.giovankov.tinytaps.notification

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.giovankov.tinytaps.domain.repository.KickSessionRepository
import com.giovankov.tinytaps.domain.repository.MovementRepository
import com.giovankov.tinytaps.domain.repository.SettingsRepository
import com.giovankov.tinytaps.util.DateUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

@HiltWorker
class InactivityCheckWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val settingsRepository: SettingsRepository,
    private val movementRepository: MovementRepository,
    private val kickSessionRepository: KickSessionRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val settings = settingsRepository.settings.first()

        // Check if feature is enabled
        if (!settings.inactivityAlertEnabled) return Result.success()

        // Check if within active window
        if (!DateUtils.isInActiveWindow(settings.activeWindowStart, settings.activeWindowEnd)) {
            return Result.success()
        }

        // Get last movement time
        val lastEpisodeEnd = movementRepository.getLastEpisodeEndTime().first()
        val lastKick = kickSessionRepository.getLastKickTime().first()
        val lastMovement = listOfNotNull(lastEpisodeEnd, lastKick).maxOrNull()

        if (lastMovement == null) return Result.success()

        val hoursSinceLastMovement = (System.currentTimeMillis() - lastMovement) / (60 * 60 * 1000)

        if (hoursSinceLastMovement >= settings.inactivityThresholdHrs) {
            NotificationHelper.showInactivityAlert(
                applicationContext,
                hoursSinceLastMovement.toInt()
            )
        }

        return Result.success()
    }

    companion object {
        private const val WORK_NAME = "inactivity_check"

        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<InactivityCheckWorker>(
                15, TimeUnit.MINUTES
            ).build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }

        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
    }
}
