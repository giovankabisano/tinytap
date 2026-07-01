package com.giovankov.tinytaps.widget

import android.content.Context
import androidx.room.Room
import com.giovankov.tinytaps.data.local.db.TinyTapsDatabase

data class WidgetDisplayState(
    val isRecording: Boolean = false,
    val recordingStartTime: Long = 0L,
    val lastMovementTime: Long? = null
)

suspend fun Context.getWidgetState(): WidgetDisplayState {
    val db = WidgetDatabase.get(applicationContext)
    val episodeDao = db.movementEpisodeDao()
    val kickDao = db.kickDao()

    val activeEpisode = episodeDao.getActiveEpisodeOnce()
    val lastEpisodeEnd = episodeDao.getLastEpisodeEndTimeOnce()
    val lastKick = kickDao.getLastKickTimeOnce()
    val lastMovement = listOfNotNull(lastEpisodeEnd, lastKick).maxOrNull()

    return WidgetDisplayState(
        isRecording = activeEpisode != null,
        recordingStartTime = activeEpisode?.startAt ?: 0L,
        lastMovementTime = lastMovement
    )
}

object WidgetDatabase {
    @Volatile
    private var instance: TinyTapsDatabase? = null

    fun get(context: Context): TinyTapsDatabase {
        return instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                TinyTapsDatabase::class.java,
                "tinytaps.db"
            ).build().also { instance = it }
        }
    }
}
