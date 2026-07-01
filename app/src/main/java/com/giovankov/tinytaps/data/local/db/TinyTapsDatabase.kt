package com.giovankov.tinytaps.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.giovankov.tinytaps.data.local.db.dao.KickDao
import com.giovankov.tinytaps.data.local.db.dao.KickSessionDao
import com.giovankov.tinytaps.data.local.db.dao.MovementEpisodeDao
import com.giovankov.tinytaps.data.local.db.entity.KickEntity
import com.giovankov.tinytaps.data.local.db.entity.KickSessionEntity
import com.giovankov.tinytaps.data.local.db.entity.MovementEpisodeEntity

@Database(
    entities = [
        MovementEpisodeEntity::class,
        KickSessionEntity::class,
        KickEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class TinyTapsDatabase : RoomDatabase() {
    abstract fun movementEpisodeDao(): MovementEpisodeDao
    abstract fun kickSessionDao(): KickSessionDao
    abstract fun kickDao(): KickDao
}
