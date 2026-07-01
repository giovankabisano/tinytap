package com.giovankov.tinytaps.domain.model

data class MovementEpisode(
    val id: String,
    val startAt: Long,
    val endAt: Long? = null,
    val durationSec: Int? = null,
    val source: MovementSource,
    val note: String? = null
)
