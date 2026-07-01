package com.giovankov.tinytaps.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.giovankov.tinytaps.MainActivity
import com.giovankov.tinytaps.R

object NotificationHelper {

    const val CHANNEL_INACTIVITY = "inactivity_alert"
    const val CHANNEL_DAILY_REMINDER = "daily_reminder"

    private const val NOTIFICATION_INACTIVITY_ID = 1001
    private const val NOTIFICATION_REMINDER_ID = 1002

    fun createChannels(context: Context) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val inactivityChannel = NotificationChannel(
            CHANNEL_INACTIVITY,
            context.getString(R.string.notification_channel_inactivity),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Pengingat jika belum ada gerakan yang dicatat"
        }

        val reminderChannel = NotificationChannel(
            CHANNEL_DAILY_REMINDER,
            context.getString(R.string.notification_channel_reminder),
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Pengingat harian untuk memperhatikan gerakan bayi"
        }

        manager.createNotificationChannels(listOf(inactivityChannel, reminderChannel))
    }

    fun showInactivityAlert(context: Context, hours: Int) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_INACTIVITY)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(context.getString(R.string.notification_inactivity_title))
            .setContentText(context.getString(R.string.notification_inactivity_text, hours))
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(context.getString(R.string.notification_inactivity_text, hours))
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        manager.notify(NOTIFICATION_INACTIVITY_ID, notification)
    }

    fun showDailyReminder(context: Context) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_DAILY_REMINDER)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(context.getString(R.string.notification_daily_reminder_title))
            .setContentText(context.getString(R.string.notification_daily_reminder_text))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        manager.notify(NOTIFICATION_REMINDER_ID, notification)
    }
}
