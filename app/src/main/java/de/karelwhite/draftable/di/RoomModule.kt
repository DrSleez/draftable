package de.karelwhite.draftable.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import de.karelwhite.draftable.data.repository.room.DraftableDatabase
import de.karelwhite.draftable.data.repository.room.HostDao
import de.karelwhite.draftable.data.repository.room.MatchDao
import de.karelwhite.draftable.data.repository.room.PlayerDao
import de.karelwhite.draftable.data.repository.room.TournamentDao

@Module
@InstallIn(SingletonComponent::class) // Dependencies live as long as the application
object RoomModule {

    @Provides
    fun provideRoomDatabase(@ApplicationContext context: Context): DraftableDatabase {
    return Room.databaseBuilder(
        context,
        DraftableDatabase::class.java,
        "draftable.db")
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()

    }

    @Provides
    fun provideHostDao(database: DraftableDatabase) : HostDao {
        return database.hostDao()
    }

    @Provides
    fun provideTournamentDao(database: DraftableDatabase) : TournamentDao {
        return database.tournamentDao()
    }

    @Provides
    fun providePlayerDao(database: DraftableDatabase) : PlayerDao {
        return database.playerDao()
    }

    @Provides
    fun provideMatchDao(database: DraftableDatabase) : MatchDao {
        return database.matchDao()
    }
}