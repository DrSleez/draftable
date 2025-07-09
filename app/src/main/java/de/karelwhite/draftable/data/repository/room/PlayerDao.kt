package de.karelwhite.draftable.data.repository.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert

@Dao
interface PlayerDao {

    @Upsert
    suspend fun insertPlayer(player: PlayerEntity)

    @Update
    suspend fun updatePlayer(player: PlayerEntity)

    @Delete
    suspend fun deletePlayer(player: PlayerEntity)
    @Query("SELECT * FROM my_players WHERE tournament_id = :tournamentId")
    suspend fun getPlayersForTournament(tournamentId: String): List<PlayerEntity>?
}