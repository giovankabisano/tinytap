package com.giovankov.tinytaps.data.repository

import com.giovankov.tinytaps.data.local.db.dao.MovementEpisodeDao
import com.giovankov.tinytaps.data.local.db.entity.MovementEpisodeEntity
import com.giovankov.tinytaps.domain.model.MovementEpisode
import com.giovankov.tinytaps.domain.model.MovementSource
import com.giovankov.tinytaps.domain.repository.MovementRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovementRepositoryImpl @Inject constructor(
    private val dao: MovementEpisodeDao
) : MovementRepository {

    override suspend fun startEpisode(source: MovementSource): MovementEpisode {
        val entity = MovementEpisodeEntity(
            id = UUID.randomUUID().toString(),
            startAt = System.currentTimeMillis(),
            source = source.name
        )
        dao.insert(entity)
        return entity.toDomain()
    }

    override suspend fun stopEpisode(id: String): MovementEpisode {
        val entity = dao.getActiveEpisodeOnce()
            ?: throw IllegalStateException("No active episode found")
        val now = System.currentTimeMillis()
        val durationSec = ((now - entity.startAt) / 1000).toInt()
        val updated = entity.copy(endAt = now, durationSec = durationSec)
        dao.update(updated)
        return updated.toDomain()
    }

    override suspend fun deleteEpisode(id: String) {
        dao.deleteById(id)
    }

    override fun getActiveEpisode(): Flow<MovementEpisode?> =
        dao.getActiveEpisode().map { it?.toDomain() }

    override suspend fun getActiveEpisodeOnce(): MovementEpisode? =
        dao.getActiveEpisodeOnce()?.toDomain()

    override fun getLastEpisodeEndTime(): Flow<Long?> =
        dao.getLastEpisodeEndTime()

    override fun getEpisodesSince(dayStart: Long): Flow<List<MovementEpisode>> =
        dao.getEpisodesSince(dayStart).map { list -> list.map { it.toDomain() } }

    override fun getAllEpisodes(): Flow<List<MovementEpisode>> =
        dao.getAllEpisodes().map { list -> list.map { it.toDomain() } }

    override fun getEpisodeCountSince(dayStart: Long): Flow<Int> =
        dao.getEpisodeCountSince(dayStart)

    override fun getEpisodeStartTimesSince(since: Long): Flow<List<Long>> =
        dao.getEpisodeStartTimesSince(since)

    private fun MovementEpisodeEntity.toDomain() = MovementEpisode(
        id = id,
        startAt = startAt,
        endAt = endAt,
        durationSec = durationSec,
        source = MovementSource.valueOf(source),
        note = note
    )
}
