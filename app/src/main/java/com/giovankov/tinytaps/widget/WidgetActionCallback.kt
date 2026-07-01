package com.giovankov.tinytaps.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.action.ActionCallback
import com.giovankov.tinytaps.data.local.db.entity.MovementEpisodeEntity
import java.util.UUID

class WidgetStartRecordingCallback : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val db = WidgetDatabase.get(context.applicationContext)
        val dao = db.movementEpisodeDao()

        if (dao.getActiveEpisodeOnce() != null) return

        dao.insert(
            MovementEpisodeEntity(
                id = UUID.randomUUID().toString(),
                startAt = System.currentTimeMillis(),
                source = "WIDGET"
            )
        )

        updateAllWidgets(context.applicationContext)
    }
}

class WidgetStopRecordingCallback : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val db = WidgetDatabase.get(context.applicationContext)
        val dao = db.movementEpisodeDao()

        val active = dao.getActiveEpisodeOnce() ?: return
        val now = System.currentTimeMillis()

        dao.update(
            active.copy(
                endAt = now,
                durationSec = ((now - active.startAt) / 1000).toInt()
            )
        )

        updateAllWidgets(context.applicationContext)
    }
}

private suspend fun updateAllWidgets(context: Context) {
    val manager = GlanceAppWidgetManager(context)
    val glanceIds = manager.getGlanceIds(TinyTapsWidget::class.java)
    glanceIds.forEach { id ->
        TinyTapsWidget().update(context, id)
    }
}
