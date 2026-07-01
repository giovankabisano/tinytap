package com.giovankov.tinytaps.notification

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.giovankov.tinytaps.domain.repository.SettingsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.time.LocalTime
import java.util.concurrent.TimeUnit

@HiltWorker
class DailyReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val settingsRepository: SettingsRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val settings = settingsRepository.settings.first()

        if (!settings.dailyReminderEnabled) return Result.success()

        val now = LocalTime.now()
        val shouldRemind = settings.dailyReminderTimes.any { reminderTime ->
            val diffMinutes = java.time.Duration.between(reminderTime, now).toMinutes()
            diffMinutes in 0..14 // Within 15 minute window
        }

        if (shouldRemind) {
            NotificationHelper.showDailyReminder(applicationContext)
        }

        return Result.success()
    }

    companion object {
        private const val WORK_NAME = "daily_reminder"

        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<DailyReminderWorker>(
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
