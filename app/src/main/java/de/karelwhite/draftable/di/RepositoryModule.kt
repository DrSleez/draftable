package de.karelwhite.draftable.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.karelwhite.draftable.data.repository.local.HostRepositoryRoomImp
import de.karelwhite.draftable.data.repository.local.TournamentRepositoryRoomImp
import de.karelwhite.draftable.data.repository.room.HostDao
import de.karelwhite.draftable.data.repository.room.MatchDao
import de.karelwhite.draftable.data.repository.room.PlayerDao
import de.karelwhite.draftable.data.repository.room.TournamentDao
import de.karelwhite.draftable.domain.repository.HostRepository
import de.karelwhite.draftable.domain.repository.TournamentRepository

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    fun provideHostRepository(hostDao: HostDao) : HostRepository {
        return HostRepositoryRoomImp(hostDao)
    }

    @Provides
    fun provideTournamentRepository(playerDao: PlayerDao, matchDao: MatchDao, tournamentDao: TournamentDao) : TournamentRepository {
        return TournamentRepositoryRoomImp(playerDao, matchDao, tournamentDao)
    }
}