package com.giovankov.tinytaps.widget

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.GlanceAppWidgetManager
import kotlinx.coroutines.runBlocking

class WidgetTimerRefreshReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        runBlocking {
            val manager = GlanceAppWidgetManager(context)
            val glanceIds = manager.getGlanceIds(TinyTapsWidget::class.java)
            glanceIds.forEach { id ->
                TinyTapsWidget().update(context, id)
            }
        }
    }

    companion object {
        private const val ACTION = "com.giovankov.tinytaps.WIDGET_TIMER_REFRESH"
        private const val INTERVAL_MS = 60_000L // 1 minute

        fun schedule(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, WidgetTimerRefreshReceiver::class.java).apply {
                action = ACTION
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.setRepeating(
                AlarmManager.RTC,
                System.currentTimeMillis() + INTERVAL_MS,
                INTERVAL_MS,
                pendingIntent
            )
        }

        fun cancel(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, WidgetTimerRefreshReceiver::class.java).apply {
                action = ACTION
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
        }
    }
}
