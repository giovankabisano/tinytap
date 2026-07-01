package com.giovankov.tinytaps.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import com.giovankov.tinytaps.data.local.db.TinyTapsDatabase
import com.giovankov.tinytaps.data.local.db.entity.MovementEpisodeEntity
import androidx.room.Room
import java.util.UUID

class WidgetStartRecordingCallback : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val db = getDatabase(context)
        val dao = db.movementEpisodeDao()

        // Check if already recording
        val active = dao.getActiveEpisodeOnce()
        if (active != null) return

        val now = System.currentTimeMillis()
        val id = UUID.randomUUID().toString()

        val entity = MovementEpisodeEntity(
            id = id,
            startAt = now,
            source = "WIDGET"
        )
        dao.insert(entity)

        // Update widget state
        context.updateWidgetState(
            isRecording = true,
            activeEpisodeId = id,
            recordingStartTime = now
        )

        // Update widget display + start periodic timer refresh
        TinyTapsWidget().update(context, glanceId)
        WidgetTimerRefreshReceiver.schedule(context)
    }
}

class WidgetStopRecordingCallback : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val db = getDatabase(context)
        val dao = db.movementEpisodeDao()

        val active = dao.getActiveEpisodeOnce() ?: return
        val now = System.currentTimeMillis()
        val durationSec = ((now - active.startAt) / 1000).toInt()

        val updated = active.copy(endAt = now, durationSec = durationSec)
        dao.update(updated)

        // Update widget state
        context.updateWidgetState(
            clearRecording = true,
            lastMovementTime = now
        )

        // Update widget display + stop periodic timer refresh
        TinyTapsWidget().update(context, glanceId)
        WidgetTimerRefreshReceiver.cancel(context)
    }
}

private var cachedDb: TinyTapsDatabase? = null

private fun getDatabase(context: Context): TinyTapsDatabase {
    return cachedDb ?: Room.databaseBuilder(
        context.applicationContext,
        TinyTapsDatabase::class.java,
        "tinytaps.db"
    ).build().also { cachedDb = it }
}
