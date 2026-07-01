package com.giovankov.tinytaps.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.GlanceAppWidgetManager
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
        val appContext = context.applicationContext
        val db = getDatabase(appContext)
        val dao = db.movementEpisodeDao()

        val active = dao.getActiveEpisodeOnce()
        if (active != null) return

        val now = System.currentTimeMillis()
        val id = UUID.randomUUID().toString()

        dao.insert(
            MovementEpisodeEntity(
                id = id,
                startAt = now,
                source = "WIDGET"
            )
        )

        appContext.updateWidgetState(
            isRecording = true,
            activeEpisodeId = id,
            recordingStartTime = now
        )

        updateAllWidgets(appContext)
    }
}

class WidgetStopRecordingCallback : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val appContext = context.applicationContext
        val db = getDatabase(appContext)
        val dao = db.movementEpisodeDao()

        val active = dao.getActiveEpisodeOnce() ?: return
        val now = System.currentTimeMillis()
        val durationSec = ((now - active.startAt) / 1000).toInt()

        dao.update(active.copy(endAt = now, durationSec = durationSec))

        appContext.updateWidgetState(
            clearRecording = true,
            lastMovementTime = now
        )

        updateAllWidgets(appContext)
    }
}

private suspend fun updateAllWidgets(context: Context) {
    val manager = GlanceAppWidgetManager(context)
    val glanceIds = manager.getGlanceIds(TinyTapsWidget::class.java)
    glanceIds.forEach { id ->
        TinyTapsWidget().update(context, id)
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
