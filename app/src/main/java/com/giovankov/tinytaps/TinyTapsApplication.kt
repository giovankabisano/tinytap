package com.giovankov.tinytaps

import android.app.Application
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.giovankov.tinytaps.notification.DailyReminderWorker
import com.giovankov.tinytaps.notification.InactivityCheckWorker
import com.giovankov.tinytaps.notification.NotificationHelper
import com.giovankov.tinytaps.widget.TinyTapsWidget
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
        refreshWidgets()
    }

    private fun refreshWidgets() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val manager = GlanceAppWidgetManager(this@TinyTapsApplication)
                val glanceIds = manager.getGlanceIds(TinyTapsWidget::class.java)
                glanceIds.forEach { id ->
                    TinyTapsWidget().update(this@TinyTapsApplication, id)
                }
            } catch (_: Exception) {
                // No widgets placed
            }
        }
    }
}
