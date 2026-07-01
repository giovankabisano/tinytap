package com.giovankov.tinytaps.domain.repository

import com.giovankov.tinytaps.domain.model.MovementEpisode
import com.giovankov.tinytaps.domain.model.MovementSource
import kotlinx.coroutines.flow.Flow

interface MovementRepository {
    suspend fun startEpisode(source: MovementSource): MovementEpisode
    suspend fun stopEpisode(id: String): MovementEpisode
    suspend fun deleteEpisode(id: String)
    fun getActiveEpisode(): Flow<MovementEpisode?>
    suspend fun getActiveEpisodeOnce(): MovementEpisode?
    fun getLastEpisodeEndTime(): Flow<Long?>
    fun getEpisodesSince(dayStart: Long): Flow<List<MovementEpisode>>
    fun getAllEpisodes(): Flow<List<MovementEpisode>>
    fun getEpisodeCountSince(dayStart: Long): Flow<Int>
    fun getEpisodeStartTimesSince(since: Long): Flow<List<Long>>
}
