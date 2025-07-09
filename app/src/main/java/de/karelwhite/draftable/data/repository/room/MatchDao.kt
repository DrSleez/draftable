package de.karelwhite.draftable.data.repository.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert

@Dao
interface MatchDao {

    @Upsert
    suspend fun insertMatch(match: MatchEntity)

    @Update
    suspend fun updateMatch(match: MatchEntity)

    @Delete
    suspend fun deleteMatch(match: MatchEntity)

    @Query("SELECT * FROM my_matches WHERE tournament_id = :tournamentId")
    suspend fun getAllMatchesForTournament(tournamentId: String): List<MatchEntity>?
}