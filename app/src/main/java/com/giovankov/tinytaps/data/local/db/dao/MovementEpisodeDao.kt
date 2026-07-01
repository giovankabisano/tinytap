package com.giovankov.tinytaps.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.giovankov.tinytaps.data.local.db.entity.MovementEpisodeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MovementEpisodeDao {

    @Insert
    suspend fun insert(episode: MovementEpisodeEntity)

    @Update
    suspend fun update(episode: MovementEpisodeEntity)

    @Query("SELECT * FROM movement_episodes WHERE endAt IS NULL LIMIT 1")
    fun getActiveEpisode(): Flow<MovementEpisodeEntity?>

    @Query("SELECT * FROM movement_episodes WHERE endAt IS NULL LIMIT 1")
    suspend fun getActiveEpisodeOnce(): MovementEpisodeEntity?

    @Query("SELECT * FROM movement_episodes WHERE startAt >= :dayStart ORDER BY startAt DESC")
    fun getEpisodesSince(dayStart: Long): Flow<List<MovementEpisodeEntity>>

    @Query("SELECT * FROM movement_episodes ORDER BY startAt DESC")
    fun getAllEpisodes(): Flow<List<MovementEpisodeEntity>>

    @Query("SELECT * FROM movement_episodes ORDER BY startAt DESC LIMIT :limit OFFSET :offset")
    fun getEpisodesPaged(limit: Int, offset: Int): Flow<List<MovementEpisodeEntity>>

    @Query("SELECT MAX(endAt) FROM movement_episodes WHERE endAt IS NOT NULL")
    fun getLastEpisodeEndTime(): Flow<Long?>

    @Query("SELECT MAX(endAt) FROM movement_episodes WHERE endAt IS NOT NULL")
    suspend fun getLastEpisodeEndTimeOnce(): Long?

    @Query("SELECT COUNT(*) FROM movement_episodes WHERE startAt >= :dayStart")
    fun getEpisodeCountSince(dayStart: Long): Flow<Int>

    @Query("""
        SELECT startAt FROM movement_episodes
        WHERE startAt >= :since
        ORDER BY startAt ASC
    """)
    fun getEpisodeStartTimesSince(since: Long): Flow<List<Long>>

    @Query("DELETE FROM movement_episodes WHERE id = :id")
    suspend fun deleteById(id: String)
}
