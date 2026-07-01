package com.giovankov.tinytaps.domain.model

data class KickSession(
    val id: String,
    val startAt: Long,
    val endAt: Long? = null,
    val targetCount: Int = 10,
    val reachedTargetAt: Long? = null,
    val status: SessionStatus,
    val kicks: List<Kick> = emptyList()
)
