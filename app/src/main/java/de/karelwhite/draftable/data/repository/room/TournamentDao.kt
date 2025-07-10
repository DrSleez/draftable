package de.karelwhite.draftable.data.repository.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import de.karelwhite.draftable.domain.model.Tournament

@Dao
interface TournamentDao {
    @Upsert
    suspend fun insertTournament(tournament : TournamentEntity)

    @Update
    suspend fun updateTournament(tournament : TournamentEntity)

    @Delete
    suspend fun deleteTournament(tournament: TournamentEntity)

    @Query("SELECT * FROM my_tournaments")
    suspend fun getAllTournaments(): List<TournamentEntity>

    @Query("SELECT * FROM my_tournaments WHERE id = :id")
    suspend fun getTournamentById(id: String) : TournamentEntity?

    @Query("SELECT * FROM my_tournaments WHERE hostPlayerId = :hostId")
    suspend fun getAllTournamentsByHostId(hostId: String) : List<TournamentEntity>
}