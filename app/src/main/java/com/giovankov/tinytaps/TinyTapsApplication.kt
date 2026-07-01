package com.giovankov.tinytaps

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.giovankov.tinytaps.notification.DailyReminderWorker
import com.giovankov.tinytaps.notification.InactivityCheckWorker
import com.giovankov.tinytaps.notification.NotificationHelper
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class TinyTapsApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createChannels(this)
        InactivityCheckWorker.schedule(this)
        DailyReminderWorker.schedule(this)
    }
}
