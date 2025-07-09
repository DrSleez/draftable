package de.karelwhite.draftable.domain.repository

import de.karelwhite.draftable.domain.model.Tournament

interface TournamentRepository {

    suspend fun createTournament(tournament: Tournament)
    suspend fun getAllTournaments() : List<Tournament>?

    suspend fun getAllTournamentsByHostId(hostId: String) : List<Tournament>?
    suspend fun getTournamentById(id: String): Tournament?
    suspend fun updateTournament(tournament: Tournament)
    suspend fun deleteTournament(id: String)
    suspend fun startTournament(tournament: Tournament)
    suspend fun stopTournament(tournament: Tournament)

}