package com.giovankov.tinytaps.di

import android.content.Context
import androidx.room.Room
import com.giovankov.tinytaps.data.local.db.TinyTapsDatabase
import com.giovankov.tinytaps.data.local.db.dao.KickDao
import com.giovankov.tinytaps.data.local.db.dao.KickSessionDao
import com.giovankov.tinytaps.data.local.db.dao.MovementEpisodeDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TinyTapsDatabase =
        Room.databaseBuilder(
            context,
            TinyTapsDatabase::class.java,
            "tinytaps.db"
        ).build()

    @Provides
    fun provideMovementEpisodeDao(db: TinyTapsDatabase): MovementEpisodeDao =
        db.movementEpisodeDao()

    @Provides
    fun provideKickSessionDao(db: TinyTapsDatabase): KickSessionDao =
        db.kickSessionDao()

    @Provides
    fun provideKickDao(db: TinyTapsDatabase): KickDao =
        db.kickDao()
}
