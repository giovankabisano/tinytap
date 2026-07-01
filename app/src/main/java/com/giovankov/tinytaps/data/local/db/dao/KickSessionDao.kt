package com.giovankov.tinytaps.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.giovankov.tinytaps.data.local.db.entity.KickSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface KickSessionDao {

    @Insert
    suspend fun insert(session: KickSessionEntity)

    @Update
    suspend fun update(session: KickSessionEntity)

    @Query("SELECT * FROM kick_sessions WHERE status = 'RUNNING' LIMIT 1")
    fun getActiveSession(): Flow<KickSessionEntity?>

    @Query("SELECT * FROM kick_sessions WHERE status = 'RUNNING' LIMIT 1")
    suspend fun getActiveSessionOnce(): KickSessionEntity?

    @Query("SELECT * FROM kick_sessions ORDER BY startAt DESC")
    fun getAllSessions(): Flow<List<KickSessionEntity>>

    @Query("SELECT * FROM kick_sessions WHERE startAt >= :dayStart ORDER BY startAt DESC")
    fun getSessionsSince(dayStart: Long): Flow<List<KickSessionEntity>>

    @Query("SELECT COUNT(*) FROM kick_sessions WHERE startAt >= :dayStart")
    fun getSessionCountSince(dayStart: Long): Flow<Int>

    @Query("""
        SELECT * FROM kick_sessions
        WHERE status = 'COMPLETED' AND reachedTargetAt IS NOT NULL AND startAt >= :since
        ORDER BY startAt ASC
    """)
    fun getCompletedSessionsSince(since: Long): Flow<List<KickSessionEntity>>

    @Query("DELETE FROM kick_sessions WHERE id = :id")
    suspend fun deleteById(id: String)
}
