package com.giovankov.tinytaps.domain.repository

import com.giovankov.tinytaps.domain.model.Kick
import com.giovankov.tinytaps.domain.model.KickSession
import kotlinx.coroutines.flow.Flow

interface KickSessionRepository {
    suspend fun startSession(targetCount: Int): KickSession
    suspend fun recordKick(sessionId: String): Kick
    suspend fun endSession(sessionId: String, abandoned: Boolean = false): KickSession
    fun getActiveSession(): Flow<KickSession?>
    suspend fun getActiveSessionOnce(): KickSession?
    fun getKickCountForSession(sessionId: String): Flow<Int>
    fun getKickTimestamps(sessionId: String): Flow<List<Long>>
    fun getLastKickTime(): Flow<Long?>
    fun getAllSessions(): Flow<List<KickSession>>
    fun getSessionsSince(dayStart: Long): Flow<List<KickSession>>
    fun getSessionCountSince(dayStart: Long): Flow<Int>
    fun getCompletedSessionsSince(since: Long): Flow<List<KickSession>>
}
