package com.giovankov.tinytaps.data.repository

import com.giovankov.tinytaps.data.local.db.dao.KickDao
import com.giovankov.tinytaps.data.local.db.dao.KickSessionDao
import com.giovankov.tinytaps.data.local.db.entity.KickEntity
import com.giovankov.tinytaps.data.local.db.entity.KickSessionEntity
import com.giovankov.tinytaps.domain.model.Kick
import com.giovankov.tinytaps.domain.model.KickSession
import com.giovankov.tinytaps.domain.model.SessionStatus
import com.giovankov.tinytaps.domain.repository.KickSessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KickSessionRepositoryImpl @Inject constructor(
    private val sessionDao: KickSessionDao,
    private val kickDao: KickDao
) : KickSessionRepository {

    override suspend fun startSession(targetCount: Int): KickSession {
        val entity = KickSessionEntity(
            id = UUID.randomUUID().toString(),
            startAt = System.currentTimeMillis(),
            targetCount = targetCount,
            status = SessionStatus.RUNNING.name
        )
        sessionDao.insert(entity)
        return entity.toDomain()
    }

    override suspend fun recordKick(sessionId: String): Kick {
        val kickEntity = KickEntity(
            id = UUID.randomUUID().toString(),
            sessionId = sessionId,
            at = System.currentTimeMillis()
        )
        kickDao.insert(kickEntity)
        return kickEntity.toDomain()
    }

    override suspend fun endSession(sessionId: String, abandoned: Boolean): KickSession {
        val session = sessionDao.getActiveSessionOnce()
            ?: throw IllegalStateException("No active session found")
        val updated = session.copy(
            endAt = System.currentTimeMillis(),
            status = if (abandoned) SessionStatus.ABANDONED.name else SessionStatus.COMPLETED.name
        )
        sessionDao.update(updated)
        return updated.toDomain()
    }

    override fun getActiveSession(): Flow<KickSession?> =
        sessionDao.getActiveSession().map { it?.toDomain() }

    override suspend fun getActiveSessionOnce(): KickSession? =
        sessionDao.getActiveSessionOnce()?.toDomain()

    override fun getKickCountForSession(sessionId: String): Flow<Int> =
        kickDao.getKickCountForSession(sessionId)

    override fun getKickTimestamps(sessionId: String): Flow<List<Long>> =
        kickDao.getKickTimestamps(sessionId)

    override fun getLastKickTime(): Flow<Long?> =
        kickDao.getLastKickTime()

    override fun getAllSessions(): Flow<List<KickSession>> =
        sessionDao.getAllSessions().map { list -> list.map { it.toDomain() } }

    override fun getSessionsSince(dayStart: Long): Flow<List<KickSession>> =
        sessionDao.getSessionsSince(dayStart).map { list -> list.map { it.toDomain() } }

    override fun getSessionCountSince(dayStart: Long): Flow<Int> =
        sessionDao.getSessionCountSince(dayStart)

    override fun getCompletedSessionsSince(since: Long): Flow<List<KickSession>> =
        sessionDao.getCompletedSessionsSince(since).map { list -> list.map { it.toDomain() } }

    private fun KickSessionEntity.toDomain() = KickSession(
        id = id,
        startAt = startAt,
        endAt = endAt,
        targetCount = targetCount,
        reachedTargetAt = reachedTargetAt,
        status = SessionStatus.valueOf(status)
    )

    private fun KickEntity.toDomain() = Kick(
        id = id,
        sessionId = sessionId,
        at = at
    )
}
