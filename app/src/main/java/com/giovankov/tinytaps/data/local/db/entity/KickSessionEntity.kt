package com.giovankov.tinytaps.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "kick_sessions")
data class KickSessionEntity(
    @PrimaryKey val id: String,
    val startAt: Long,
    val endAt: Long? = null,
    val targetCount: Int = 10,
    val reachedTargetAt: Long? = null,
    val status: String
)
