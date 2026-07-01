package com.giovankov.tinytaps.data.local.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "kicks",
    foreignKeys = [
        ForeignKey(
            entity = KickSessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("sessionId")]
)
data class KickEntity(
    @PrimaryKey val id: String,
    val sessionId: String,
    val at: Long
)
