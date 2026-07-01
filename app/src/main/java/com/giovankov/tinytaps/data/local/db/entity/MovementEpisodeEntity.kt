package com.giovankov.tinytaps.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movement_episodes")
data class MovementEpisodeEntity(
    @PrimaryKey val id: String,
    val startAt: Long,
    val endAt: Long? = null,
    val durationSec: Int? = null,
    val source: String,
    val note: String? = null
)
