package com.giovankov.tinytaps.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.giovankov.tinytaps.data.local.db.entity.KickEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface KickDao {

    @Insert
    suspend fun insert(kick: KickEntity)

    @Query("SELECT * FROM kicks WHERE sessionId = :sessionId ORDER BY at ASC")
    fun getKicksForSession(sessionId: String): Flow<List<KickEntity>>

    @Query("SELECT COUNT(*) FROM kicks WHERE sessionId = :sessionId")
    fun getKickCountForSession(sessionId: String): Flow<Int>

    @Query("SELECT MAX(at) FROM kicks")
    fun getLastKickTime(): Flow<Long?>

    @Query("SELECT at FROM kicks WHERE sessionId = :sessionId ORDER BY at ASC")
    fun getKickTimestamps(sessionId: String): Flow<List<Long>>

    @Query("DELETE FROM kicks WHERE sessionId = :sessionId")
    suspend fun deleteBySessionId(sessionId: String)
}
