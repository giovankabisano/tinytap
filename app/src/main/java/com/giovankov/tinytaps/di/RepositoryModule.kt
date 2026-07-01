package com.giovankov.tinytaps.di

import com.giovankov.tinytaps.data.repository.KickSessionRepositoryImpl
import com.giovankov.tinytaps.data.repository.MovementRepositoryImpl
import com.giovankov.tinytaps.data.repository.SettingsRepositoryImpl
import com.giovankov.tinytaps.domain.repository.KickSessionRepository
import com.giovankov.tinytaps.domain.repository.MovementRepository
import com.giovankov.tinytaps.domain.repository.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMovementRepository(impl: MovementRepositoryImpl): MovementRepository

    @Binds
    @Singleton
    abstract fun bindKickSessionRepository(impl: KickSessionRepositoryImpl): KickSessionRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository
}
